package com.example.aqimonitor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.aqimonitor.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateAQIValue()
            handler.postDelayed(this, 5000) // Update every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start updating AQI value
        updateAQIValue()
        handler.post(updateRunnable)
    }

    private fun updateAQIValue() {
        val randomAQI = Random.nextInt(0, 500)
        binding.aqiValue.text = randomAQI.toString()
        
        // Update circle color based on AQI value
        val color = when {
            randomAQI <= 50 -> getColor(R.color.good)
            randomAQI <= 100 -> getColor(R.color.moderate)
            randomAQI <= 150 -> getColor(R.color.unhealthy_sensitive)
            randomAQI <= 200 -> getColor(R.color.unhealthy)
            randomAQI <= 300 -> getColor(R.color.very_unhealthy)
            else -> getColor(R.color.hazardous)
        }
        binding.aqiCircle.setCardBackgroundColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
} 