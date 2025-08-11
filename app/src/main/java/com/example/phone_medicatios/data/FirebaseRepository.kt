package com.example.phone_medicatios.data

import android.util.Log
import java.util.*

class FirebaseRepository : Repository {
    private val firebaseService = FirebaseService()
    private val currentUserId = "user1" // Por ahora hardcodeado, después se puede implementar autenticación

    companion object {
        private const val TAG = "FirebaseRepository"
    }

    override suspend fun getMedications(): List<Medication> {
        return try {
            firebaseService.getMedications(currentUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting medications: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getReminders(): List<Reminder> {
        return try {
            firebaseService.getReminders(currentUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reminders: ${e.message}")
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