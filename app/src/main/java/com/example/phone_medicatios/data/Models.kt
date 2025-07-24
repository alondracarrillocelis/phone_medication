package com.example.phone_medicatios.data

import java.util.Date

// Modelo para medicamentos (colección separada)
data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val unit: String = "",
    val type: String = "",
    val description: String = "",
    val instructions: String = "",
    val userId: String = "",
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val totalReminders: Int = 0
)

// Modelo para recordatorios (colección separada)
data class Reminder(
    val id: String = "",
    val medicationId: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val unit: String = "",
    val type: String = "",
    val frequency: String = "",
    val firstDoseTime: String = "",
    val doseTime: String = "",
    val hoursBetweenDoses: String = "",
    val selectedDays: String = "",
    val cycleWeeks: String = "",
    val userId: String = "",
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val schedules: List<ReminderSchedule> = emptyList(),
    val totalDoses: Int = 0,
    val completedDoses: Int = 0
)

// Modelo para horarios de recordatorios
data class ReminderSchedule(
    val id: String = "",
    val reminderId: String = "",
    val time: String = "",
    val dosage: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Date? = null,
    val scheduledDate: Date = Date()
)

// Modelo para datos del formulario
data class ReminderFormData(
    val medication: String = "",
    val dosage: String = "",
    val unit: String = "mg",
    val type: String = "Tableta",
    val frequency: String = "Diariamente",
    val firstDoseTime: String = "8:00 a.m.",
    val doseTime: String = "8:00 p.m.",
    val hoursBetweenDoses: String = "",
    val selectedDays: List<String> = emptyList(),
    val cycleWeeks: String = "",
    val description: String = "",
    val instructions: String = ""
)

// Modelo para horarios del día
data class TodaySchedule(
    val id: String = "",
    val reminderId: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val time: String = "",
    val isCompleted: Boolean = false,
    val isOverdue: Boolean = false
)

// Modelo para estadísticas
data class MedicationStats(
    val totalMedications: Int = 0,
    val activeReminders: Int = 0,
    val completedToday: Int = 0,
    val pendingToday: Int = 0
) 