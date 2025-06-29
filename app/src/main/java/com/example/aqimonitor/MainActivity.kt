package com.example.aqimonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aqimonitor.shared.bluetooth.BluetoothAqiReader
import com.example.aqimonitor.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAqiReader: BluetoothAqiReader
    private val appVersion = "1.0.1" // Version number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display version
        binding.versionText.text = "v$appVersion"

        bluetoothAqiReader = BluetoothAqiReader(this)

        lifecycleScope.launch {
            bluetoothAqiReader.aqiFlow.collect { data ->
                data?.let { updateAQIValue(it.value) }
            }
        }
    }

    private fun updateAQIValue(value: Int) {
        binding.aqiValue.text = value.toString()

        // Update circle color based on AQI value
        val color = when {
            value <= 50 -> getColor(R.color.good)
            value <= 100 -> getColor(R.color.moderate)
            value <= 150 -> getColor(R.color.unhealthy_sensitive)
            value <= 200 -> getColor(R.color.unhealthy)
            value <= 300 -> getColor(R.color.very_unhealthy)
            else -> getColor(R.color.hazardous)
        }
        binding.aqiCircle.setCardBackgroundColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAqiReader.stop()
    }

    override fun onStart() {
        super.onStart()
        bluetoothAqiReader.start()
    }

    override fun onStop() {
        super.onStop()
        bluetoothAqiReader.stop()
    }
}
