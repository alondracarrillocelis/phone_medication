package com.example.phone_medicatios.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface Repository {
    // Medicamentos
    suspend fun getMedications(): List<Medication>
    suspend fun addMedication(medication: Medication): String?
    suspend fun updateMedication(medication: Medication): Boolean
    suspend fun deleteMedication(medicationId: String): Boolean
    
    // Recordatorios
    suspend fun getReminders(): List<Reminder>
    suspend fun addReminder(reminder: Reminder): String?
    suspend fun updateReminder(reminder: Reminder): Boolean
    suspend fun deleteReminder(reminder: Reminder): Boolean
    
    // Horarios
    suspend fun getTodaySchedules(): List<TodaySchedule>
    suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean
    suspend fun markDoseAsCompleted(reminderId: String, doseIndex: Int): Boolean
    
    // Estadísticas
    suspend fun getUserStats(): MedicationStats
}
