package com.example.aqimonitor.shared.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import com.example.aqimonitor.shared.AqiData
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Scans for an ESP32 based AQI sensor over BLE and exposes readings via [StateFlow].
 */
class BluetoothAqiReader(
    private val context: Context,
    private val serviceUuid: UUID = DEFAULT_SERVICE_UUID,
    private val characteristicUuid: UUID = DEFAULT_CHARACTERISTIC_UUID
) {
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    private val scanner get() = bluetoothAdapter?.bluetoothLeScanner

    private var gatt: BluetoothGatt? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _aqiFlow = MutableStateFlow<AqiData?>(null)
    /** Current AQI value emitted whenever a new reading arrives. */
    val aqiFlow: StateFlow<AqiData?> = _aqiFlow.asStateFlow()

    /** Start scanning for the sensor device. */
    fun start() {
        val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(serviceUuid)).build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner?.startScan(listOf(filter), settings, scanCallback)
    }

    /** Stop scanning/notifications and close the connection. */
    fun stop() {
        scanner?.stopScan(scanCallback)
        gatt?.close()
        gatt = null
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            connect(result.device)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.firstOrNull()?.device?.let { connect(it) }
        }
    }

    private fun connect(device: BluetoothDevice) {
        stop()
        gatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                this@BluetoothAqiReader.gatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val characteristic = gatt
                .getService(serviceUuid)
                ?.getCharacteristic(characteristicUuid)
            characteristic?.let {
                gatt.setCharacteristicNotification(it, true)
                val descriptor = it.getDescriptor(CLIENT_CONFIG_DESCRIPTOR)
                descriptor?.let { desc ->
                    desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(desc)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == characteristicUuid) {
                val value = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8,
                    0
                ) ?: return
                scope.launch { _aqiFlow.emit(AqiData(value)) }
            }
        }
    }

    companion object {
        /** Default service UUID used by the ESP32 sensor. */
        val DEFAULT_SERVICE_UUID: UUID = UUID.fromString("0000181A-0000-1000-8000-00805f9b34fb")
        /** Characteristic containing the AQI value. */
        val DEFAULT_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A6E-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CONFIG_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
