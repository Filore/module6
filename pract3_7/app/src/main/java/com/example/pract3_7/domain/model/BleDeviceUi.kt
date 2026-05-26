package com.example.pract3_7.domain.model

import android.bluetooth.BluetoothDevice

data class BleDeviceUi(
    val name: String,
    val address: String,
    val rssi: Int,
    val device: BluetoothDevice?
)
