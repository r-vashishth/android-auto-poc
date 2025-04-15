package com.example.aqimonitor.automotive

import android.os.Handler
import android.os.Looper
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.random.Random

class AqiScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {
    
    private var aqiValue = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = Runnable { updateAqi() }
    private val appVersion = "1.0.1" // Version number
    
    init {
        lifecycle.addObserver(this)
        updateAqi()
    }
    
    override fun onGetTemplate(): Template {
        val aqiRow = Row.Builder()
            .setTitle("Current AQI")
            .addText(getAqiValueWithCategory())
            .build()
            
        val versionRow = Row.Builder()
            .setTitle("App Version (Automotive)")
            .addText("v$appVersion")
            .build()
            
        val pane = Pane.Builder()
            .addRow(aqiRow)
            .addRow(versionRow)
            .build()
            
        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.APP_ICON)
            .setTitle("AQI Monitor Auto")
            .build()
    }
    
    private fun updateAqi() {
        aqiValue = Random.nextInt(0, 500)
        invalidate()
        handler.postDelayed(updateRunnable, 5000) // Update every 5 seconds
    }
    
    private fun getAqiValueWithCategory(): String {
        val category = when {
            aqiValue <= 50 -> "Good"
            aqiValue <= 100 -> "Moderate"
            aqiValue <= 150 -> "Unhealthy for Sensitive Groups"
            aqiValue <= 200 -> "Unhealthy"
            aqiValue <= 300 -> "Very Unhealthy"
            else -> "Hazardous"
        }
        
        return "$aqiValue - $category"
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handler.removeCallbacks(updateRunnable)
    }
} 