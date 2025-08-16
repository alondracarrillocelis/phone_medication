package com.example.phone_medicatios.data

import java.util.*

data class MedicineReminder(
    var id: String = "",
    val name: String = "",
    val dosage: String = "",
    val unit: String = "",
    val type: String = "",
    val description: String = "",
    val instructions: String = "",
    val frequencyPerDay: Int = 1,
    val dosagePerIntake: String = "",
    val firstHour: String = "",
    var hourList: List<String> = emptyList(),
    var hoursList: List<String> = emptyList(),
    val days: List<String> = emptyList(),
    val cycleWeeks: String = "",
    val startDate: Date? = null,
    val endDate: Date? = null,
    val safeStartDate: Date? = null,
    val safeEndDate: Date? = null,
    val userId: String = "",
    val createdAt: Date = Date(),
    val active: Boolean = true,
    val completed: Boolean = false
) {
    fun calculateSchedule() {
        // Lógica para calcular horarios automáticamente
        if (frequencyPerDay > 0 && firstHour.isNotEmpty()) {
            val hours = mutableListOf<String>()
            val firstHourInt = firstHour.toIntOrNull() ?: 8
            
            for (i in 0 until frequencyPerDay) {
                val hour = (firstHourInt + (i * (24 / frequencyPerDay))) % 24
                hours.add("$hour:00")
            }
            
            hourList = hours
            hoursList = hours
        }
    }
}
