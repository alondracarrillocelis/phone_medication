package com.example.phone_medicatios.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    


    // Obtener medicamentos en tiempo real usando addSnapshotListener
    fun getMedicationsFlow(userId: String): Flow<List<Medication>> = callbackFlow {
        Log.d(TAG, "=== INICIANDO FLUJO DE MEDICAMENTOS ===")
        Log.d(TAG, "UserId: $userId")
        Log.d(TAG, "Colección: $MEDICATIONS_COLLECTION")
        
        val listener = db.collection(MEDICATIONS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error en listener de medicamentos: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    Log.d(TAG, "Documentos encontrados en Firestore: ${snapshot.documents.size}")
                    
                    val medications = snapshot.documents.mapNotNull { doc ->
                        Log.d(TAG, "Procesando documento: ${doc.id}")
                        val medication = doc.toObject(Medication::class.java)?.copy(id = doc.id)
                        if (medication != null) {
                            Log.d(TAG, "Medicamento mapeado: ${medication.name} (ID: ${medication.id}, userId: ${medication.userId})")
                        } else {
                            Log.e(TAG, "Error mapeando documento: ${doc.id}")
                        }
                        medication
                    }
                    
                    Log.d(TAG, "Medicamentos finales: ${medications.size}")
                    medications.forEach { med ->
                        Log.d(TAG, "  - ${med.name} (ID: ${med.id}, userId: ${med.userId})")
                    }
                    
                    trySend(medications)
                }
            }
        
        awaitClose {
            Log.d(TAG, "Cerrando listener de medicamentos")
            listener.remove()
        }
    }

    // Obtener recordatorios en tiempo real usando addSnapshotListener
    fun getRemindersFlow(userId: String): Flow<List<Reminder>> = callbackFlow {
        Log.d(TAG, "=== INICIANDO FLUJO DE RECORDATORIOS ===")
        Log.d(TAG, "UserId: $userId")
        Log.d(TAG, "Colección: $REMINDERS_COLLECTION")
        
        val listener = db.collection(REMINDERS_COLLECTION)
            .whereEqualTo("userId", userId)
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
                        val reminder = doc.toObject(Reminder::class.java)?.copy(id = doc.id)
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

    // Obtener todos los medicamentos de un usuario (método legacy para compatibilidad)
    suspend fun getMedications(userId: String): List<Medication> {
        return try {
            Log.d(TAG, "=== OBTENIENDO MEDICAMENTOS (LEGACY) ===")
            Log.d(TAG, "UserId: $userId")
            Log.d(TAG, "Colección: $MEDICATIONS_COLLECTION")
            
            val snapshot = db.collection(MEDICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Documentos encontrados en Firestore: ${snapshot.documents.size}")
            
            val medications = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Procesando documento: ${doc.id}")
                val medication = doc.toObject(Medication::class.java)?.copy(id = doc.id)
                if (medication != null) {
                    Log.d(TAG, "Medicamento mapeado: ${medication.name} (ID: ${medication.id}, userId: ${medication.userId})")
                } else {
                    Log.e(TAG, "Error mapeando documento: ${doc.id}")
                }
                medication
            }
            
            Log.d(TAG, "Medicamentos finales: ${medications.size}")
            medications.forEach { med ->
                Log.d(TAG, "  - ${med.name} (ID: ${med.id}, userId: ${med.userId})")
            }
            medications
        } catch (e: Exception) {
            Log.e(TAG, "Error getting medications: ${e.message}", e)
            emptyList()
        }
    }

    // Obtener todos los recordatorios de un usuario (método legacy para compatibilidad)
    suspend fun getReminders(userId: String): List<Reminder> {
        return try {
            Log.d(TAG, "=== OBTENIENDO RECORDATORIOS (LEGACY) ===")
            Log.d(TAG, "UserId: $userId")
            Log.d(TAG, "Colección: $REMINDERS_COLLECTION")
            
            val snapshot = db.collection(REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Documentos encontrados en Firestore: ${snapshot.documents.size}")
            
            val reminders = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Procesando documento: ${doc.id}")
                val reminder = doc.toObject(Reminder::class.java)?.copy(id = doc.id)
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
            Log.e(TAG, "Error getting reminders: ${e.message}", e)
            emptyList()
        }
    }

    // Obtener recordatorios del día actual
    suspend fun getTodaySchedules(userId: String): List<TodaySchedule> {
        return try {
            val snapshot = db.collection(REMINDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("active", true)
                .get()
                .await()

            val todaySchedules = mutableListOf<TodaySchedule>()
            
            snapshot.documents.forEach { doc ->
                val reminder = doc.toObject(Reminder::class.java)
                if (reminder != null) {
                    Log.d(TAG, "Procesando recordatorio: ${reminder.name}, frecuencia: ${reminder.frequencyHours} horas")
                    
                    // Calcular horarios basados en la frecuencia
                    val calculatedHours = reminder.calculateSchedule()
                    Log.d(TAG, "Horarios calculados para ${reminder.name}: $calculatedHours")
                    
                    calculatedHours.forEachIndexed { index, hour ->
                        val scheduleId = "${doc.id}_${index}"
                        val isDoseCompleted = reminder.isDoseCompleted(index)
                        todaySchedules.add(
                            TodaySchedule(
                                id = scheduleId,
                                reminderId = doc.id,
                                medicationName = reminder.name,
                                dosage = reminder.dosage.toString(),
                                time = hour,
                                isCompleted = isDoseCompleted, // Usar estado individual de la dosis
                                isOverdue = isTimeOverdue(hour)
                            )
                        )
                        Log.d(TAG, "Agregada dosis $index: $hour para ${reminder.name} (completada: $isDoseCompleted)")
                    }
                }
            }
            
            Log.d(TAG, "Horarios de hoy generados: ${todaySchedules.size}")
            todaySchedules.forEach { schedule ->
                Log.d(TAG, "  - ${schedule.medicationName} a las ${schedule.time} (ID: ${schedule.id})")
            }
            todaySchedules
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today schedules: ${e.message}")
            emptyList()
        }
    }

    // Función para verificar si una hora está atrasada
    private fun isTimeOverdue(timeString: String): Boolean {
        return try {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis
            
            // Parsear la hora del recordatorio
            val reminderTime = parseTimeToMillis(timeString)
            
            // Si la hora del recordatorio ya pasó hoy, está atrasada
            reminderTime < now
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if time is overdue: ${e.message}")
            false
        }
    }

    // Función para parsear tiempo a milisegundos
    private fun parseTimeToMillis(time: String): Long {
        val calendar = Calendar.getInstance()
        val cleaned = time.replace("a.m.", "AM").replace("p.m.", "PM").replace(" ", "")
        val formats = listOf("h:mma", "hh:mma", "H:mm", "HH:mm")
        
        for (format in formats) {
            try {
                val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                val date = sdf.parse(cleaned)
                if (date != null) {
                    val now = Calendar.getInstance()
                    calendar.time = date
                    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    return calendar.timeInMillis
                }
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
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
            Log.d(TAG, "Intentando agregar recordatorio: ${reminder.name}")
            Log.d(TAG, "Datos del recordatorio: userId=${reminder.userId}, frequencyHours=${reminder.frequencyHours}")
            
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
            Log.d(TAG, "Marcando recordatorio como completado: $reminderId, completed: $completed")
            
            // Actualizar el recordatorio
            db.collection(REMINDERS_COLLECTION)
                .document(reminderId)
                .update(
                    mapOf(
                        "completed" to completed,
                        "active" to !completed
                    )
                )
                .await()
            
            Log.d(TAG, "Recordatorio marcado como completado: $reminderId, completed: $completed")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking reminder completed: ${e.message}")
            false
        }
    }

    // Marcar dosis específica como completada
    suspend fun markDoseAsCompleted(reminderId: String, doseIndex: Int): Boolean {
        return try {
            Log.d(TAG, "Marcando dosis $doseIndex como completada para recordatorio: $reminderId")
            
            // Obtener el recordatorio actual
            val docRef = db.collection(REMINDERS_COLLECTION).document(reminderId)
            val doc = docRef.get().await()
            
            if (doc.exists()) {
                val reminder = doc.toObject(Reminder::class.java)
                if (reminder != null) {
                    val totalDoses = reminder.getTotalDosesCount()
                    Log.d(TAG, "Recordatorio encontrado: ${reminder.name}, dosis totales: $totalDoses")
                    
                    // Crear nueva lista de dosis completadas
                    val newCompletedDoses = if (reminder.completedDoses.contains(doseIndex)) {
                        reminder.completedDoses
                    } else {
                        reminder.completedDoses + doseIndex
                    }
                    
                    Log.d(TAG, "Dosis completadas actuales: ${reminder.completedDoses}, nueva lista: $newCompletedDoses")
                    
                    // Verificar si todas las dosis están completadas
                    val allCompleted = newCompletedDoses.size >= totalDoses
                    
                    // Actualizar el recordatorio con la nueva dosis completada
                    val updateData = mutableMapOf<String, Any>(
                        "completedDoses" to newCompletedDoses,
                        "completed" to allCompleted
                    )
                    
                    // Si todas las dosis están completadas, marcar como inactivo
                    if (allCompleted) {
                        updateData["active"] = false
                    }
                    
                    docRef.update(updateData).await()
                    
                    Log.d(TAG, "Dosis $doseIndex marcada como completada exitosamente. Progreso: ${newCompletedDoses.size}/$totalDoses")
                    return true
                }
            }
            
            Log.e(TAG, "Recordatorio no encontrado: $reminderId")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error marking dose as completed: ${e.message}")
            false
        }
    }

    // Obtener estadísticas del usuario
    suspend fun getUserStats(userId: String): MedicationStats {
        return try {
            val medications = getMedications(userId)
            val reminders = getReminders(userId)
            
            val activeReminders = reminders.count { it.active }
            
            // Calcular dosis completadas y pendientes basadas en dosis individuales
            var totalCompletedDoses = 0
            var totalPendingDoses = 0
            
            reminders.forEach { reminder ->
                if (reminder.active) {
                    val completedDoses = reminder.getCompletedDosesCount()
                    val totalDoses = reminder.getTotalDosesCount()
                    totalCompletedDoses += completedDoses
                    totalPendingDoses += (totalDoses - completedDoses)
                }
            }

            val stats = MedicationStats(
                totalMedications = medications.size,
                activeReminders = activeReminders,
                completedToday = totalCompletedDoses,
                pendingToday = totalPendingDoses
            )
            
            Log.d(TAG, "Estadísticas calculadas: $stats (dosis completadas: $totalCompletedDoses, pendientes: $totalPendingDoses)")
            stats
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stats: ${e.message}", e)
            MedicationStats()
        }
    }
} 