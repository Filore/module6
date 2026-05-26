package com.example.pract3_7.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.example.pract3_7.domain.model.BleDeviceUi
import java.util.UUID

@SuppressLint("MissingPermission")
class HeartRateBleManager(
    context: Context,
    private val onDeviceFound: (BleDeviceUi) -> Unit,
    private val onScanningChanged: (Boolean) -> Unit,
    private val onStatusChanged: (String) -> Unit,
    private val onConnectedDeviceChanged: (BleDeviceUi?) -> Unit,
    private val onHeartRateChanged: (Int?) -> Unit,
    private val onHeartRateCharacteristicChanged: (Boolean) -> Unit
) {
    private val appContext = context.applicationContext
    private val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner get() = bluetoothAdapter?.bluetoothLeScanner
    private val mainHandler = Handler(Looper.getMainLooper())

    private var bluetoothGatt: BluetoothGatt? = null
    private var heartRateCharacteristic: BluetoothGattCharacteristic? = null
    private var isScanning = false
    private var wasConnected = false

    val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val address = device.address ?: return
            val name = result.scanRecord?.deviceName ?: device.name ?: "Unknown device"

            onMain {
                onDeviceFound(
                    BleDeviceUi(
                        name = name,
                        address = address,
                        rssi = result.rssi,
                        device = device
                    )
                )
            }
        }

        override fun onScanFailed(errorCode: Int) {
            isScanning = false
            onMain {
                onScanningChanged(false)
                onStatusChanged("Ошибка сканирования: $errorCode")
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, statusCode: Int, newState: Int) {
            onMain {
                onStatusChanged(
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            if (statusCode == BluetoothGatt.GATT_SUCCESS) {
                                wasConnected = true
                                "Connected"
                            } else {
                                "Connection error: ${gattStatusText(statusCode)}"
                            }
                        }
                        BluetoothProfile.STATE_CONNECTING -> "Connecting"
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            if (statusCode == BluetoothGatt.GATT_SUCCESS) {
                                if (wasConnected) {
                                    "Disconnected after connect: ${gattStatusText(statusCode)}"
                                } else {
                                    "Disconnected"
                                }
                            } else {
                                "Disconnected: ${gattStatusText(statusCode)}"
                            }
                        }
                        else -> "Статус подключения: $newState"
                    }
                )
            }

            if (statusCode == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                wasConnected = false
                heartRateCharacteristic = null
                onMain {
                    onConnectedDeviceChanged(null)
                    onHeartRateChanged(null)
                    onHeartRateCharacteristicChanged(false)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, statusCode: Int) {
            if (statusCode != BluetoothGatt.GATT_SUCCESS) {
                onMain { onStatusChanged("Не удалось обнаружить GATT-сервисы") }
                return
            }

            val characteristic = gatt
                .getService(HEART_RATE_SERVICE_UUID)
                ?.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)

            if (characteristic == null) {
                onMain { onStatusChanged("Heart Rate Service не найден") }
                return
            }

            heartRateCharacteristic = characteristic
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
            if (descriptor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            } else {
                readHeartRateIfAvailable(gatt, characteristic)
            }

            onMain {
                onHeartRateCharacteristicChanged(true)
                onStatusChanged("Connected: ждем Heart Rate Measurement")
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            statusCode: Int
        ) {
            if (descriptor.uuid != CLIENT_CHARACTERISTIC_CONFIG_UUID) return

            if (statusCode == BluetoothGatt.GATT_SUCCESS) {
                val characteristic = heartRateCharacteristic ?: return
                readHeartRateIfAvailable(gatt, characteristic)
            } else {
                onMain { onStatusChanged("CCC write error: ${gattStatusText(statusCode)}") }
            }
        }

        @Deprecated("Used on Android 12 and lower")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            handleHeartRate(characteristic.uuid, characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            handleHeartRate(characteristic.uuid, value)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            statusCode: Int
        ) {
            if (statusCode == BluetoothGatt.GATT_SUCCESS) {
                handleHeartRate(characteristic.uuid, value)
            }
        }

        @Deprecated("Used on Android 12 and lower")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            statusCode: Int
        ) {
            if (statusCode == BluetoothGatt.GATT_SUCCESS) {
                handleHeartRate(characteristic.uuid, characteristic.value)
            }
        }
    }

    fun startScan() {
        disconnect()
        scanner?.startScan(scanCallback)
        isScanning = true
        onScanningChanged(true)
        onConnectedDeviceChanged(null)
        onHeartRateChanged(null)
        onHeartRateCharacteristicChanged(false)
        onStatusChanged("Scanning")
    }

    fun stopScan(status: String = "Сканирование остановлено") {
        if (isScanning) {
            scanner?.stopScan(scanCallback)
            isScanning = false
            onScanningChanged(false)
            onStatusChanged(status)
        }
    }

    fun connect(device: BleDeviceUi) {
        stopScan(status = "Connecting")
        onConnectedDeviceChanged(device)
        onHeartRateChanged(null)
        onHeartRateCharacteristicChanged(false)
        onStatusChanged("Connecting")
        bluetoothGatt?.close()
        wasConnected = false
        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.device?.connectGatt(appContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.device?.connectGatt(appContext, false, gattCallback)
        }
    }

    fun refreshHeartRate() {
        val characteristic = heartRateCharacteristic ?: return
        val canRead = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
        if (canRead) {
            bluetoothGatt?.readCharacteristic(characteristic)
            onStatusChanged("Запрос данных")
        } else {
            onStatusChanged("Устройство отправляет пульс через уведомления")
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        heartRateCharacteristic = null
        wasConnected = false
        onConnectedDeviceChanged(null)
        onHeartRateChanged(null)
        onHeartRateCharacteristicChanged(false)
    }

    fun release() {
        stopScan(status = "Сканирование остановлено")
        disconnect()
    }

    private fun handleHeartRate(uuid: UUID, value: ByteArray) {
        if (uuid != HEART_RATE_MEASUREMENT_UUID) return
        parseHeartRate(value)?.let { bpm ->
            onMain {
                onHeartRateChanged(bpm)
                onStatusChanged("Данные обновлены")
            }
        }
    }

    private fun readHeartRateIfAvailable(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        val canRead = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
        if (canRead) {
            gatt.readCharacteristic(characteristic)
            onMain { onStatusChanged("Connected: читаем Heart Rate") }
        } else {
            onMain { onStatusChanged("Connected: ждём notification из LightBlue") }
        }
    }

    private fun onMain(action: () -> Unit) {
        mainHandler.post(action)
    }

    companion object {
        private val HEART_RATE_SERVICE_UUID: UUID =
            UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val HEART_RATE_MEASUREMENT_UUID: UUID =
            UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        fun requiredPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } else {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        private fun parseHeartRate(value: ByteArray): Int? {
            if (value.size < 2) return null
            val flags = value[0].toInt()
            return if (flags and 0x01 == 0) {
                value[1].toInt() and 0xFF
            } else {
                if (value.size < 3) null else {
                    (value[1].toInt() and 0xFF) or ((value[2].toInt() and 0xFF) shl 8)
                }
            }
        }

        private fun gattStatusText(statusCode: Int): String {
            return when (statusCode) {
                BluetoothGatt.GATT_SUCCESS -> "status 0"
                8 -> "status 8, timeout"
                19 -> "status 19, remote disconnected"
                22 -> "status 22, local host disconnected"
                62 -> "status 62, connection failed"
                133 -> "status 133, Android GATT error"
                else -> "status $statusCode"
            }
        }
    }
}
