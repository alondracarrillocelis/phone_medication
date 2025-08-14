package com.example.phone_medicatios.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date

class TestRepository(context: Context) {
    
    init {
        Log.d("TestRepository", "Inicializando TestRepository (sin base de datos)")
    }
    
    // ==================== MEDICAMENTOS ====================
    
    suspend fun saveMedication(medication: Medication): Result<String> {
        return try {
            Log.d("TestRepository", "Guardando medicamento (simulado): ${medication.name}")
            // Simular guardado exitoso
            Thread.sleep(100) // Simular delay
            Result.success("test_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al guardar medicamento", e)
            Result.failure(e)
        }
    }
    
    fun getMedications(userId: String): Flow<List<Medication>> = flow {
        Log.d("TestRepository", "Obteniendo medicamentos (simulado) para userId: $userId")
        // Retornar lista vacía para pruebas
        val medications = emptyList<Medication>()
        Log.d("TestRepository", "Medicamentos obtenidos: ${medications.size}")
        emit(medications)
    }.flowOn(Dispatchers.IO)
    
    suspend fun deleteMedication(medicationId: String): Result<Unit> {
        return try {
            Log.d("TestRepository", "Eliminando medicamento (simulado): $medicationId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al eliminar medicamento", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateMedication(medication: Medication): Result<Unit> {
        return try {
            Log.d("TestRepository", "Actualizando medicamento (simulado): ${medication.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al actualizar medicamento", e)
            Result.failure(e)
        }
    }
    
    // ==================== RECORDATORIOS ====================
    
    suspend fun saveReminder(reminder: Reminder): Result<String> {
        return try {
            Log.d("TestRepository", "Guardando recordatorio (simulado): ${reminder.name}")
            Result.success("test_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al guardar recordatorio", e)
            Result.failure(e)
        }
    }
    
    fun getReminders(userId: String): Flow<List<Reminder>> = flow {
        Log.d("TestRepository", "Obteniendo recordatorios (simulado) para userId: $userId")
        val reminders = emptyList<Reminder>()
        Log.d("TestRepository", "Recordatorios obtenidos: ${reminders.size}")
        emit(reminders)
    }.flowOn(Dispatchers.IO)
    
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            Log.d("TestRepository", "Eliminando recordatorio (simulado): $reminderId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al eliminar recordatorio", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            Log.d("TestRepository", "Actualizando recordatorio (simulado): ${reminder.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al actualizar recordatorio", e)
            Result.failure(e)
        }
    }
    
    // ==================== HORARIOS ====================
    
    suspend fun saveReminderSchedules(schedules: List<ReminderSchedule>): Result<Unit> {
        return try {
            Log.d("TestRepository", "Guardando horarios (simulado): ${schedules.size}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al guardar horarios", e)
            Result.failure(e)
        }
    }
    
    fun getTodaySchedules(userId: String): Flow<List<TodaySchedule>> = flow {
        Log.d("TestRepository", "Obteniendo horarios del día (simulado) para userId: $userId")
        val todaySchedules = emptyList<TodaySchedule>()
        Log.d("TestRepository", "Horarios del día obtenidos: ${todaySchedules.size}")
        emit(todaySchedules)
    }.flowOn(Dispatchers.IO)
    
    suspend fun markScheduleAsCompleted(scheduleId: String): Result<Unit> {
        return try {
            Log.d("TestRepository", "Marcando horario como completado (simulado): $scheduleId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al marcar horario como completado", e)
            Result.failure(e)
        }
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    suspend fun getMedicationStats(userId: String): Result<MedicationStats> {
        return try {
            Log.d("TestRepository", "Obteniendo estadísticas (simulado) para userId: $userId")
            
            val stats = MedicationStats(
                totalMedications = 0,
                activeReminders = 0,
                completedToday = 0,
                pendingToday = 0
            )
            
            Log.d("TestRepository", "Estadísticas obtenidas: $stats")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("TestRepository", "Error al obtener estadísticas", e)
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
            Log.d("TestRepository", "[createReminderWithSchedules] Proceso simulado exitoso")
            Result.success("test_reminder_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TestRepository", "[createReminderWithSchedules] Error en el proceso", e)
            Result.failure(e)
        }
    }
} 