package com.example.pract3_7.presentation.scanner

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pract3_7.data.HeartRateBleManager
import com.example.pract3_7.domain.model.BleDeviceUi
import com.example.pract3_7.ui.theme.Pract3_7Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen() {
    val context = LocalContext.current
    val requiredPermissions = remember { HeartRateBleManager.requiredPermissions() }
    val devices = remember { mutableStateListOf<BleDeviceUi>() }
    var hasPermissions by remember { mutableStateOf(context.hasPermissions(requiredPermissions)) }
    var isScanning by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Готово к сканированию") }
    var connectedDevice by remember { mutableStateOf<BleDeviceUi?>(null) }
    var heartRate by remember { mutableStateOf<Int?>(null) }
    var canRefreshHeartRate by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermissions = requiredPermissions.all { result[it] == true || context.hasPermission(it) }
        status = if (hasPermissions) "Разрешения получены" else "Нужны разрешения Bluetooth"
    }

    val bleManager = remember {
        HeartRateBleManager(
            context = context,
            onDeviceFound = { item ->
                val index = devices.indexOfFirst { it.address == item.address }
                if (index >= 0) {
                    devices[index] = item
                } else {
                    devices.add(item)
                }
            },
            onScanningChanged = { isScanning = it },
            onStatusChanged = { status = it },
            onConnectedDeviceChanged = { connectedDevice = it },
            onHeartRateChanged = { heartRate = it },
            onHeartRateCharacteristicChanged = { canRefreshHeartRate = it }
        )
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    DisposableEffect(Unit) {
        onDispose { bleManager.release() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Heart Rate Monitor") }) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeartRateCard(
                heartRate = heartRate,
                status = status,
                connectedDevice = connectedDevice
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = hasPermissions && bleManager.isBluetoothEnabled,
                    onClick = {
                        if (isScanning) {
                            bleManager.stopScan()
                        } else {
                            devices.clear()
                            bleManager.startScan()
                        }
                    }
                ) {
                    Text(if (isScanning) "Остановить" else "Начать сканирование")
                }

                Button(
                    enabled = connectedDevice != null,
                    onClick = {
                        bleManager.disconnect()
                        status = "Disconnected"
                    }
                ) {
                    Text("Отключиться")
                }
            }

            if (!hasPermissions) {
                Button(onClick = { permissionLauncher.launch(requiredPermissions) }) {
                    Text("Выдать разрешения")
                }
            } else if (!bleManager.isBluetoothSupported) {
                Text("Bluetooth не поддерживается на этом устройстве")
            } else if (!bleManager.isBluetoothEnabled) {
                Text("Включите Bluetooth на телефоне")
            }

            Button(
                enabled = canRefreshHeartRate,
                onClick = { bleManager.refreshHeartRate() }
            ) {
                Text("Обновить данные")
            }

            Text(
                text = "Найденные устройства",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (isScanning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.width(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Идет поиск BLE-устройств")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(devices, key = { it.address }) { item ->
                    DeviceRow(
                        item = item,
                        onClick = { bleManager.connect(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeartRateCard(
    heartRate: Int?,
    status: String,
    connectedDevice: BleDeviceUi?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Heart Rate: ${heartRate?.let { "$it bpm" } ?: "-"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("Status: $status")
            connectedDevice?.let {
                Text("Device: ${it.name} (${it.address})")
            }
        }
    }
}

@Composable
private fun DeviceRow(
    item: BleDeviceUi,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Text(item.address, style = MaterialTheme.typography.bodyMedium)
            Text("RSSI: ${item.rssi} dBm", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun Context.hasPermissions(permissions: Array<String>): Boolean {
    return permissions.all { hasPermission(it) }
}

private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@Preview(showBackground = true)
@Composable
fun HeartRateCardPreview() {
    Pract3_7Theme {
        HeartRateCard(
            heartRate = 72,
            status = "Connected",
            connectedDevice = BleDeviceUi("Heart Rate Sensor", "00:11:22:33:44:55", -48, null)
        )
    }
}
