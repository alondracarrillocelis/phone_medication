package com.example.phone_medicatios.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "FirebaseService"
        private const val MEDICATIONS_COLLECTION = "medications"
        private const val REMINDERS_COLLECTION = "reminders"
        private const val USERS_COLLECTION = "users"
    }

    // Obtener todos los medicamentos de un usuario
    suspend fun getMedications(userId: String): List<Medication> {
        return try {
            Log.d(TAG, "Buscando medicamentos para userId: $userId")
            
            val snapshot = db.collection(MEDICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val medications = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Medication::class.java)?.copy(id = doc.id)
            }
            
            Log.d(TAG, "Medicamentos encontrados: ${medications.size}")
            medications.forEach { med ->
                Log.d(TAG, "Medicamento: ${med.name}, userId: ${med.userId}, id: ${med.id}")
            }
            
            medications
        } catch (e: Exception) {
            Log.e(TAG, "Error getting medications: ${e.message}", e)
            emptyList()
        }
    }

    // Obtener todos los recordatorios de un usuario
    suspend fun getReminders(userId: String): List<Reminder> {
        return try {
            Log.d(TAG, "Buscando recordatorios para userId: $userId")
            
            val snapshot = db.collection(REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val reminders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Reminder::class.java)?.copy(id = doc.id)
            }
            
            Log.d(TAG, "Recordatorios encontrados: ${reminders.size}")
            reminders.forEach { reminder ->
                Log.d(TAG, "Recordatorio: ${reminder.medicationName}, userId: ${reminder.userId}, id: ${reminder.id}")
            }
            
            reminders
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reminders: ${e.message}", e)
            emptyList()
        }
    }

    // Obtener recordatorios del día actual
    suspend fun getTodaySchedules(userId: String): List<TodaySchedule> {
        return try {
            val snapshot = db.collection(REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val reminder = doc.toObject(Reminder::class.java)
                if (reminder != null) {
                    TodaySchedule(
                        id = doc.id,
                        reminderId = doc.id,
                        medicationName = reminder.medicationName,
                        dosage = reminder.dosage,
                        time = reminder.firstDoseTime,
                        isCompleted = reminder.completedDoses > 0,
                        isOverdue = false // Se puede implementar lógica de atraso más adelante
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today schedules: ${e.message}")
            emptyList()
        }
    }

    // Agregar un nuevo medicamento
    suspend fun addMedication(medication: Medication): String? {
        return try {
            Log.d(TAG, "Intentando agregar medicamento: ${medication.name}")
            Log.d(TAG, "Datos del medicamento: userId=${medication.userId}, dosage=${medication.dosage}")
            
            val docRef = db.collection(MEDICATIONS_COLLECTION).add(medication).await()
            Log.d(TAG, "Medication added with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding medication: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    // Agregar un nuevo recordatorio
    suspend fun addReminder(reminder: Reminder): String? {
        return try {
            Log.d(TAG, "Intentando agregar recordatorio: ${reminder.medicationName}")
            Log.d(TAG, "Datos del recordatorio: userId=${reminder.userId}, frequency=${reminder.frequency}")
            
            val docRef = db.collection(REMINDERS_COLLECTION).add(reminder).await()
            Log.d(TAG, "Reminder added with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding reminder: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    // Actualizar un medicamento
    suspend fun updateMedication(medication: Medication): Boolean {
        return try {
            db.collection(MEDICATIONS_COLLECTION)
                .document(medication.id)
                .set(medication)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating medication: ${e.message}")
            false
        }
    }

    // Actualizar un recordatorio
    suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            db.collection(REMINDERS_COLLECTION)
                .document(reminder.id)
                .set(reminder)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating reminder: ${e.message}")
            false
        }
    }

    // Eliminar un medicamento
    suspend fun deleteMedication(medicationId: String): Boolean {
        return try {
            db.collection(MEDICATIONS_COLLECTION)
                .document(medicationId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting medication: ${e.message}")
            false
        }
    }

    // Eliminar un recordatorio
    suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            db.collection(REMINDERS_COLLECTION)
                .document(reminderId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting reminder: ${e.message}")
            false
        }
    }

    // Marcar recordatorio como completado
    suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean {
        return try {
            db.collection(REMINDERS_COLLECTION)
                .document(reminderId)
                .update("completedDoses", if (completed) 1 else 0)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking reminder completed: ${e.message}")
            false
        }
    }

    // Obtener estadísticas del usuario
    suspend fun getUserStats(userId: String): MedicationStats {
        return try {
            val medications = getMedications(userId)
            val reminders = getReminders(userId)
            
            val activeReminders = reminders.count { it.isActive }
            val completedToday = reminders.count { it.completedDoses > 0 }
            val pendingToday = activeReminders - completedToday

            MedicationStats(
                totalMedications = medications.size,
                activeReminders = activeReminders,
                completedToday = completedToday,
                pendingToday = pendingToday
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stats: ${e.message}")
            MedicationStats()
        }
    }
} 