package com.example.aqimonitor.shared

/**
 * Shared data class for AQI information
 */
data class AqiData(
    val value: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Returns the AQI category based on value
     */
    val category: AqiCategory
        get() = when {
            value <= 50 -> AqiCategory.GOOD
            value <= 100 -> AqiCategory.MODERATE
            value <= 150 -> AqiCategory.UNHEALTHY_SENSITIVE
            value <= 200 -> AqiCategory.UNHEALTHY
            value <= 300 -> AqiCategory.VERY_UNHEALTHY
            else -> AqiCategory.HAZARDOUS
        }
}

/**
 * Enum representing AQI categories
 */
enum class AqiCategory(val label: String, val colorName: String) {
    GOOD("Good", "good"),
    MODERATE("Moderate", "moderate"),
    UNHEALTHY_SENSITIVE("Unhealthy for Sensitive Groups", "unhealthy_sensitive"),
    UNHEALTHY("Unhealthy", "unhealthy"),
    VERY_UNHEALTHY("Very Unhealthy", "very_unhealthy"),
    HAZARDOUS("Hazardous", "hazardous")
} 