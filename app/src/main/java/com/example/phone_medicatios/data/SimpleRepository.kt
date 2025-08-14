package com.example.phone_medicatios.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

class SimpleRepository(context: Context) {
    private val dbHelper: SimpleDatabaseHelper
    
    init {
        try {
            Log.d("SimpleRepository", "Inicializando SimpleRepository...")
            dbHelper = SimpleDatabaseHelper(context)
            Log.d("SimpleRepository", "SimpleRepository inicializado exitosamente")
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al inicializar SimpleRepository", e)
            throw e
        }
    }
    
    // ==================== MEDICAMENTOS ====================
    
    suspend fun saveMedication(medication: Medication): Result<String> {
        return try {
            Log.d("SimpleRepository", "Guardando medicamento: ${medication.name}")
            val id = dbHelper.insertMedication(medication)
            Log.d("SimpleRepository", "Medicamento guardado con ID: $id")
            Result.success(id.toString())
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al guardar medicamento", e)
            Result.failure(e)
        }
    }
    
    fun getMedications(userId: String): Flow<List<Medication>> = flow {
        Log.d("SimpleRepository", "Obteniendo medicamentos para userId: $userId")
        val medications = dbHelper.getMedications(userId)
        Log.d("SimpleRepository", "Medicamentos obtenidos: ${medications.size}")
        emit(medications)
    }.flowOn(Dispatchers.IO)
    
    suspend fun deleteMedication(medicationId: String): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Eliminando medicamento: $medicationId")
            val result = dbHelper.deleteMedication(medicationId)
            Log.d("SimpleRepository", "Medicamento eliminado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al eliminar medicamento", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateMedication(medication: Medication): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Actualizando medicamento: ${medication.name}")
            // Para SQLite simple, eliminamos y recreamos el medicamento
            dbHelper.deleteMedication(medication.id)
            val newId = dbHelper.insertMedication(medication)
            Log.d("SimpleRepository", "Medicamento actualizado exitosamente con nuevo ID: $newId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al actualizar medicamento", e)
            Result.failure(e)
        }
    }
    
    // ==================== RECORDATORIOS ====================
    
    suspend fun saveReminder(reminder: Reminder): Result<String> {
        return try {
            Log.d("SimpleRepository", "Guardando recordatorio: ${reminder.name}")
            val id = dbHelper.insertReminder(reminder)
            Log.d("SimpleRepository", "Recordatorio guardado con ID: $id")
            Result.success(id.toString())
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al guardar recordatorio", e)
            Result.failure(e)
        }
    }
    
    fun getReminders(userId: String): Flow<List<Reminder>> = flow {
        Log.d("SimpleRepository", "Obteniendo recordatorios para userId: $userId")
        val reminders = dbHelper.getReminders(userId)
        Log.d("SimpleRepository", "Recordatorios obtenidos: ${reminders.size}")
        emit(reminders)
    }.flowOn(Dispatchers.IO)
    
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Eliminando recordatorio: $reminderId")
            val result = dbHelper.deleteReminder(reminderId)
            Log.d("SimpleRepository", "Recordatorio eliminado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al eliminar recordatorio", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Actualizando recordatorio: ${reminder.name}")
            // Para SQLite simple, eliminamos y recreamos el recordatorio
            dbHelper.deleteReminder(reminder.id)
            val newId = dbHelper.insertReminder(reminder)
            Log.d("SimpleRepository", "Recordatorio actualizado exitosamente con nuevo ID: $newId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al actualizar recordatorio", e)
            Result.failure(e)
        }
    }
    
    // ==================== HORARIOS ====================
    
    suspend fun saveReminderSchedules(schedules: List<ReminderSchedule>): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Guardando ${schedules.size} horarios")
            schedules.forEach { schedule ->
                dbHelper.insertSchedule(schedule)
            }
            Log.d("SimpleRepository", "Horarios guardados exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al guardar horarios", e)
            Result.failure(e)
        }
    }
    
    fun getTodaySchedules(userId: String): Flow<List<TodaySchedule>> = flow {
        Log.d("SimpleRepository", "Obteniendo horarios del día para userId: $userId")
        val schedulesWithNames = dbHelper.getPendingSchedulesWithMedicationName()
        val todaySchedules = schedulesWithNames.map { (schedule, medicationName) ->
            TodaySchedule(
                id = schedule.id,
                reminderId = schedule.reminderId,
                medicationName = medicationName,
                dosage = schedule.dosage,
                time = schedule.time,
                isCompleted = schedule.isCompleted,
                isOverdue = false // Se calculará después
            )
        }
        Log.d("SimpleRepository", "Horarios del día obtenidos: ${todaySchedules.size}")
        emit(todaySchedules)
    }.flowOn(Dispatchers.IO)
    
    suspend fun markScheduleAsCompleted(scheduleId: String): Result<Unit> {
        return try {
            Log.d("SimpleRepository", "Marcando horario como completado: $scheduleId")
            val result = dbHelper.markScheduleAsCompleted(scheduleId)
            Log.d("SimpleRepository", "Horario marcado como completado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al marcar horario como completado", e)
            Result.failure(e)
        }
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    suspend fun getMedicationStats(userId: String): Result<MedicationStats> {
        return try {
            Log.d("SimpleRepository", "Obteniendo estadísticas para userId: $userId")
            
            // Obtener medicamentos activos
            val medications = dbHelper.getMedications(userId)
            val totalMedications = medications.size
            
            // Obtener recordatorios activos
            val reminders = dbHelper.getReminders(userId)
            val activeReminders = reminders.size
            
            // Obtener horarios del día
            val todaySchedules = dbHelper.getPendingSchedules()
            val pendingToday = todaySchedules.size
            
            val stats = MedicationStats(
                totalMedications = totalMedications,
                activeReminders = activeReminders,
                completedToday = 0, // Se calculará después
                pendingToday = pendingToday
            )
            
            Log.d("SimpleRepository", "Estadísticas obtenidas: $stats")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "Error al obtener estadísticas", e)
            Result.failure(e)
        }
    }
    
    // ==================== UTILIDADES ====================
    
    suspend fun createReminderWithSchedules(
        medication: Medication,
        formData: ReminderFormData,
        userId: String
    ): Result<String> {
        return try {
            Log.d("SimpleRepository", "[createReminderWithSchedules] Guardando medicamento...")
            // 1. Guardar medicamento
            val medicationId = saveMedication(medication).getOrThrow()
            Log.d("SimpleRepository", "[createReminderWithSchedules] Medicamento guardado con ID: $medicationId")
            
            // 2. Crear recordatorio
            val reminder = Reminder(
                name = formData.name,
                type = formData.type,
                dosage = formData.dosage,
                unit = formData.unit,
                instructions = formData.instructions,
                frequencyHours = formData.frequencyHours,
                firstHour = formData.firstHour,
                days = formData.days,
                userId = userId
            )
            Log.d("SimpleRepository", "[createReminderWithSchedules] Guardando recordatorio...")
            val reminderId = saveReminder(reminder).getOrThrow()
            Log.d("SimpleRepository", "[createReminderWithSchedules] Recordatorio guardado con ID: $reminderId")
            
            // 3. Crear horarios
            val schedules = mutableListOf<ReminderSchedule>()
            
            // Calcular horarios basados en la frecuencia
            val calculatedHours = reminder.calculateSchedule()
            calculatedHours.forEach { hour ->
                schedules.add(
                    ReminderSchedule(
                        reminderId = reminderId,
                        time = hour,
                        dosage = "${formData.dosage} ${formData.unit}"
                    )
                )
            }
            Log.d("SimpleRepository", "[createReminderWithSchedules] Guardando horarios: ${schedules.size}")
            saveReminderSchedules(schedules).getOrThrow()
            Log.d("SimpleRepository", "[createReminderWithSchedules] Horarios guardados exitosamente")
            Result.success(reminderId)
        } catch (e: Exception) {
            Log.e("SimpleRepository", "[createReminderWithSchedules] Error en el proceso", e)
            Result.failure(e)
        }
    }
} 