package com.example.phone_medicatios.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.util.*

class FirebaseRepository : Repository {
    private val firebaseService = FirebaseService()
    private val currentUserId = "user1" // Por ahora hardcodeado, después se puede implementar autenticación

    companion object {
        private const val TAG = "FirebaseRepository"
    }

    // Nuevos métodos usando Flow para tiempo real
    override fun getMedicationsFlow(): Flow<List<Medication>> {
        Log.d(TAG, "FirebaseRepository: Iniciando flujo de medicamentos para userId: $currentUserId")
        return firebaseService.getMedicationsFlow(currentUserId)
    }

    override fun getRemindersFlow(): Flow<List<Reminder>> {
        Log.d(TAG, "FirebaseRepository: Iniciando flujo de recordatorios para userId: $currentUserId")
        return firebaseService.getRemindersFlow(currentUserId)
    }

    // Métodos legacy para compatibilidad
    override suspend fun getMedications(): List<Medication> {
        return try {
            Log.d(TAG, "FirebaseRepository: Solicitando medicamentos para userId: $currentUserId")
            val medications = firebaseService.getMedications(currentUserId)
            Log.d(TAG, "FirebaseRepository: Medicamentos obtenidos: ${medications.size}")
            medications.forEach { med ->
                Log.d(TAG, "FirebaseRepository: Medicamento - ${med.name}, id: ${med.id}, userId: ${med.userId}")
            }
            medications
        } catch (e: Exception) {
            Log.e(TAG, "Error getting medications: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getReminders(): List<Reminder> {
        return try {
            Log.d(TAG, "FirebaseRepository: Solicitando recordatorios para userId: $currentUserId")
            val reminders = firebaseService.getReminders(currentUserId)
            Log.d(TAG, "FirebaseRepository: Recordatorios obtenidos: ${reminders.size}")
            reminders.forEach { reminder ->
                Log.d(TAG, "FirebaseRepository: Recordatorio - ${reminder.name}, id: ${reminder.id}, userId: ${reminder.userId}")
            }
            reminders
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reminders: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getTodaySchedules(): List<TodaySchedule> {
        return try {
            firebaseService.getTodaySchedules(currentUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today schedules: ${e.message}")
            emptyList()
        }
    }

    override suspend fun addMedication(medication: Medication): String? {
        return try {
            val medicationWithUserId = medication.copy(
                userId = currentUserId,
                createdAt = Date()
            )
            firebaseService.addMedication(medicationWithUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding medication: ${e.message}")
            null
        }
    }

    override suspend fun addReminder(reminder: Reminder): String? {
        return try {
            val reminderWithUserId = reminder.copy(
                userId = currentUserId,
                createdAt = Date()
            )
            firebaseService.addReminder(reminderWithUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding reminder: ${e.message}")
            null
        }
    }

    override suspend fun updateMedication(medication: Medication): Boolean {
        return try {
            firebaseService.updateMedication(medication)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating medication: ${e.message}")
            false
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            firebaseService.updateReminder(reminder)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating reminder: ${e.message}")
            false
        }
    }

    override suspend fun deleteMedication(medicationId: String): Boolean {
        return try {
            firebaseService.deleteMedication(medicationId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting medication: ${e.message}")
            false
        }
    }

    override suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            firebaseService.deleteReminder(reminderId)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting reminder: ${e.message}")
            false
        }
    }

    override suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean {
        return try {
            firebaseService.markReminderCompleted(reminderId, completed)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking reminder completed: ${e.message}")
            false
        }
    }

    override suspend fun markDoseAsCompleted(reminderId: String, doseIndex: Int): Boolean {
        return try {
            firebaseService.markDoseAsCompleted(reminderId, doseIndex)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking dose as completed: ${e.message}")
            false
        }
    }

    override suspend fun getUserStats(): MedicationStats {
        return try {
            firebaseService.getUserStats(currentUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stats: ${e.message}")
            MedicationStats()
        }
    }

    // Métodos adicionales específicos para Firebase
    suspend fun refreshData() {
        // Este método se puede usar para refrescar datos desde Firebase
        Log.d(TAG, "Refreshing data from Firebase")
    }

    suspend fun syncData() {
        // Este método se puede usar para sincronizar datos locales con Firebase
        Log.d(TAG, "Syncing data with Firebase")
    }
    

} 