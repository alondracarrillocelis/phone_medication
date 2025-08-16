package com.example.phone_medicatios.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "FirebaseService"
        private const val MEDICATION_REMINDERS_COLLECTION = "medicationReminders"
    }

    // Obtener recordatorios de medicamentos en tiempo real usando addSnapshotListener
    fun getMedicineRemindersFlow(userId: String): Flow<List<MedicineReminder>> = callbackFlow {
        Log.d(TAG, "=== INICIANDO FLUJO DE RECORDATORIOS DE MEDICAMENTOS ===")
        Log.d(TAG, "UserId: $userId")
        Log.d(TAG, "Colección: $MEDICATION_REMINDERS_COLLECTION")
        
        val listener = db.collection(MEDICATION_REMINDERS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("active", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error en listener de recordatorios: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    Log.d(TAG, "Documentos encontrados en Firestore: ${snapshot.documents.size}")
                    
                    val reminders = snapshot.documents.mapNotNull { doc ->
                        Log.d(TAG, "Procesando documento: ${doc.id}")
                        val reminder = doc.toObject(MedicineReminder::class.java)?.copy(id = doc.id)
                        if (reminder != null) {
                            Log.d(TAG, "Recordatorio mapeado: ${reminder.name} (ID: ${reminder.id}, userId: ${reminder.userId})")
                        } else {
                            Log.e(TAG, "Error mapeando documento: ${doc.id}")
                        }
                        reminder
                    }
                    
                    Log.d(TAG, "Recordatorios finales: ${reminders.size}")
                    reminders.forEach { rem ->
                        Log.d(TAG, "  - ${rem.name} (ID: ${rem.id}, userId: ${rem.userId})")
                    }
                    
                    trySend(reminders)
                }
            }
        
        awaitClose {
            Log.d(TAG, "Cerrando listener de recordatorios")
            listener.remove()
        }
    }

    // Obtener todos los recordatorios de medicamentos de un usuario
    suspend fun getMedicineReminders(userId: String): List<MedicineReminder> {
        return try {
            Log.d(TAG, "=== OBTENIENDO RECORDATORIOS DE MEDICAMENTOS ===")
            Log.d(TAG, "UserId: $userId")
            Log.d(TAG, "Colección: $MEDICATION_REMINDERS_COLLECTION")
            
            val snapshot = db.collection(MEDICATION_REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Documentos encontrados en Firestore: ${snapshot.documents.size}")
            
            val reminders = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Procesando documento: ${doc.id}")
                val reminder = doc.toObject(MedicineReminder::class.java)?.copy(id = doc.id)
                if (reminder != null) {
                    Log.d(TAG, "Recordatorio mapeado: ${reminder.name} (ID: ${reminder.id}, userId: ${reminder.userId})")
                } else {
                    Log.e(TAG, "Error mapeando documento: ${doc.id}")
                }
                reminder
            }
            
            Log.d(TAG, "Recordatorios finales: ${reminders.size}")
            reminders.forEach { rem ->
                Log.d(TAG, "  - ${rem.name} (ID: ${rem.id}, userId: ${rem.userId})")
            }
            
            reminders
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo recordatorios: ${e.message}")
            emptyList()
        }
    }

    // Obtener recordatorios del día actual
    suspend fun getTodaySchedules(userId: String): List<TodaySchedule> {
        return try {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            val snapshot = db.collection(MEDICATION_REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val reminder = doc.toObject(MedicineReminder::class.java)
                if (reminder != null) {
                    TodaySchedule(
                        id = doc.id,
                        reminderId = doc.id,
                        medicationName = reminder.name,
                        dosage = reminder.dosage,
                        time = reminder.firstHour,
                        isCompleted = reminder.completed,
                        isOverdue = false
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today schedules: ${e.message}")
            emptyList()
        }
    }

    // Agregar un nuevo recordatorio de medicamento
    suspend fun addMedicineReminder(reminder: MedicineReminder): String? {
        return try {
            // Calcular horarios automáticamente
            reminder.calculateSchedule()
            
            val docRef = db.collection(MEDICATION_REMINDERS_COLLECTION).add(reminder).await()
            Log.d(TAG, "Medicine reminder added with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error adding medicine reminder: ${e.message}")
            null
        }
    }

    // Actualizar un recordatorio de medicamento
    suspend fun updateMedicineReminder(reminder: MedicineReminder): Boolean {
        return try {
            // Recalcular horarios
            reminder.calculateSchedule()
            
            db.collection(MEDICATION_REMINDERS_COLLECTION)
                .document(reminder.id)
                .set(reminder)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating medicine reminder: ${e.message}")
            false
        }
    }

    // Eliminar un recordatorio de medicamento
    suspend fun deleteMedicineReminder(reminderId: String): Boolean {
        return try {
            db.collection(MEDICATION_REMINDERS_COLLECTION)
                .document(reminderId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting medicine reminder: ${e.message}")
            false
        }
    }

    // Marcar recordatorio como completado
    suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean {
        return try {
            db.collection(MEDICATION_REMINDERS_COLLECTION)
                .document(reminderId)
                .update("completed", completed)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking reminder completed: ${e.message}")
            false
        }
    }

    // Marcar recordatorio como pospuesto
    suspend fun snoozeReminder(reminderId: String): Boolean {
        return try {
            db.collection(MEDICATION_REMINDERS_COLLECTION)
                .document(reminderId)
                .update("snoozedAt", Date())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error snoozing reminder: ${e.message}")
            false
        }
    }

    // Obtener estadísticas del usuario
    suspend fun getUserStats(userId: String): MedicationStats {
        return try {
            val reminders = getMedicineReminders(userId)
            
            val activeReminders = reminders.count { it.active }
            val completedToday = reminders.count { it.completed }
            val pendingToday = activeReminders - completedToday

            MedicationStats(
                totalMedications = reminders.size,
                activeReminders = activeReminders,
                completedToday = completedToday,
                pendingToday = pendingToday
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stats: ${e.message}")
            MedicationStats()
        }
    }

    // Métodos de compatibilidad para mantener la interfaz existente
    suspend fun getMedications(userId: String): List<Medication> {
        val reminders = getMedicineReminders(userId)
        return reminders.map { reminder ->
            Medication(
                id = reminder.id,
                name = reminder.name,
                dosage = reminder.dosage,
                unit = reminder.unit,
                type = reminder.type,
                description = reminder.description,
                instructions = reminder.instructions,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.active
            )
        }
    }

    suspend fun getReminders(userId: String): List<Reminder> {
        val reminders = getMedicineReminders(userId)
        return reminders.map { reminder ->
            Reminder(
                id = reminder.id,
                name = reminder.name,
                type = reminder.type,
                dosage = reminder.dosage.toDoubleOrNull() ?: 0.0,
                unit = reminder.unit,
                instructions = reminder.instructions,
                frequencyHours = 24 / reminder.frequencyPerDay,
                firstHour = reminder.firstHour,
                days = reminder.days,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                completed = reminder.completed,
                active = reminder.active,
                completedDoses = emptyList()
            )
        }
    }

    suspend fun addMedication(medication: Medication): String? {
        val reminder = MedicineReminder(
            name = medication.name,
            dosage = medication.dosage,
            unit = medication.unit,
            type = medication.type,
            description = medication.description,
            instructions = medication.instructions,
            userId = medication.userId,
            createdAt = medication.createdAt,
            active = medication.isActive
        )
        return addMedicineReminder(reminder)
    }

    suspend fun addReminder(reminder: Reminder): String? {
        val medicineReminder = MedicineReminder(
            name = reminder.name,
            dosage = reminder.dosage.toString(),
            unit = reminder.unit,
            type = reminder.type,
            frequencyPerDay = reminder.getTotalDosesCount(),
            firstHour = reminder.firstHour,
            userId = reminder.userId,
            createdAt = reminder.createdAt,
            active = reminder.active
        )
        return addMedicineReminder(medicineReminder)
    }

    suspend fun updateMedication(medication: Medication): Boolean {
        val reminder = MedicineReminder(
            id = medication.id,
            name = medication.name,
            dosage = medication.dosage,
            unit = medication.unit,
            type = medication.type,
            description = medication.description,
            instructions = medication.instructions,
            userId = medication.userId,
            createdAt = medication.createdAt,
            active = medication.isActive
        )
        return updateMedicineReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder): Boolean {
        val medicineReminder = MedicineReminder(
            id = reminder.id,
            name = reminder.name,
            dosage = reminder.dosage.toString(),
            unit = reminder.unit,
            type = reminder.type,
            frequencyPerDay = reminder.getTotalDosesCount(),
            firstHour = reminder.firstHour,
            userId = reminder.userId,
            createdAt = reminder.createdAt,
            active = reminder.active
        )
        return updateMedicineReminder(medicineReminder)
    }

    suspend fun deleteMedication(medicationId: String): Boolean {
        return deleteMedicineReminder(medicationId)
    }

    suspend fun deleteReminder(reminderId: String): Boolean {
        return deleteMedicineReminder(reminderId)
    }
} 