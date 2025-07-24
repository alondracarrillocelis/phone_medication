package com.example.phone_medicatios.data

import android.content.Context
import android.util.Log
import com.example.phone_medicatios.data.dao.MedicationDao
import com.example.phone_medicatios.data.dao.ReminderDao
import com.example.phone_medicatios.data.dao.ReminderScheduleDao
import com.example.phone_medicatios.data.database.AppDatabase
import com.example.phone_medicatios.data.entities.MedicationEntity
import com.example.phone_medicatios.data.entities.ReminderEntity
import com.example.phone_medicatios.data.entities.ReminderScheduleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date

private data class StatsData(
    val totalMedications: Int,
    val activeReminders: Int,
    val completedToday: Int,
    val pendingToday: Int
)

class SqliteRepository(context: Context) {
    private val database: AppDatabase
    private val medicationDao: MedicationDao
    private val reminderDao: ReminderDao
    private val scheduleDao: ReminderScheduleDao

    init {
        try {
            Log.d("SqliteRepository", "Inicializando SqliteRepository...")
            database = AppDatabase.getDatabase(context)
            Log.d("SqliteRepository", "Base de datos obtenida exitosamente")
            medicationDao = database.medicationDao()
            Log.d("SqliteRepository", "MedicationDao inicializado")
            reminderDao = database.reminderDao()
            Log.d("SqliteRepository", "ReminderDao inicializado")
            scheduleDao = database.reminderScheduleDao()
            Log.d("SqliteRepository", "ReminderScheduleDao inicializado")
            Log.d("SqliteRepository", "SqliteRepository inicializado exitosamente")
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al inicializar SqliteRepository", e)
            throw e
        }
    }

    // ==================== MEDICAMENTOS ====================

    suspend fun saveMedication(medication: Medication): Result<String> {
        return try {
            Log.d("SqliteRepository", "Guardando medicamento: ${medication.name}")
            val entity = MedicationEntity(
                name = medication.name,
                dosage = medication.dosage,
                unit = medication.unit,
                type = medication.type,
                description = medication.description,
                instructions = medication.instructions,
                userId = medication.userId,
                createdAt = medication.createdAt,
                isActive = medication.isActive,
                totalReminders = medication.totalReminders
            )
            val id = withContext(Dispatchers.IO) {
                medicationDao.insertMedication(entity)
            }
            Log.d("SqliteRepository", "Medicamento guardado con ID: $id")
            Result.success(id.toString())
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al guardar medicamento", e)
            Result.failure(e)
        }
    }

    fun getMedications(userId: String): Flow<List<Medication>> {
        Log.d("SqliteRepository", "Obteniendo medicamentos para userId: $userId")
        return medicationDao.getMedicationsByUserId(userId).map { entities ->
            entities.map { entity ->
                Medication(
                    id = entity.id.toString(),
                    name = entity.name,
                    dosage = entity.dosage,
                    unit = entity.unit,
                    type = entity.type,
                    description = entity.description,
                    instructions = entity.instructions,
                    userId = entity.userId,
                    createdAt = entity.createdAt,
                    isActive = entity.isActive,
                    totalReminders = entity.totalReminders
                )
            }
        }
    }

    suspend fun deleteMedication(medicationId: String): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Eliminando medicamento: $medicationId")
            withContext(Dispatchers.IO) {
                medicationDao.deleteMedication(medicationId.toLong())
            }
            Log.d("SqliteRepository", "Medicamento eliminado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al eliminar medicamento", e)
            Result.failure(e)
        }
    }

    suspend fun updateMedication(medication: Medication): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Actualizando medicamento: ${medication.name}")
            val entity = MedicationEntity(
                id = medication.id.toLong(),
                name = medication.name,
                dosage = medication.dosage,
                unit = medication.unit,
                type = medication.type,
                description = medication.description,
                instructions = medication.instructions,
                userId = medication.userId,
                createdAt = medication.createdAt,
                isActive = medication.isActive,
                totalReminders = medication.totalReminders
            )
            withContext(Dispatchers.IO) {
                medicationDao.updateMedication(entity)
            }
            Log.d("SqliteRepository", "Medicamento actualizado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al actualizar medicamento", e)
            Result.failure(e)
        }
    }

    // ==================== RECORDATORIOS ====================

    suspend fun saveReminder(reminder: Reminder): Result<String> {
        return try {
            Log.d("SqliteRepository", "Guardando recordatorio: ${reminder.medicationName}")
            val entity = ReminderEntity(
                medicationId = reminder.medicationId.toLongOrNull() ?: 0L,
                medicationName = reminder.medicationName,
                dosage = reminder.dosage,
                unit = reminder.unit,
                type = reminder.type,
                frequency = reminder.frequency,
                firstDoseTime = reminder.firstDoseTime,
                doseTime = reminder.doseTime,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.isActive,
                totalDoses = reminder.totalDoses,
                completedDoses = reminder.completedDoses
            )
            val id = withContext(Dispatchers.IO) {
                reminderDao.insertReminder(entity)
            }
            Log.d("SqliteRepository", "Recordatorio guardado con ID: $id")
            Result.success(id.toString())
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al guardar recordatorio", e)
            Result.failure(e)
        }
    }

    fun getReminders(userId: String): Flow<List<Reminder>> {
        Log.d("SqliteRepository", "Obteniendo recordatorios para userId: $userId")
        return reminderDao.getRemindersByUserId(userId).map { entities ->
            entities.map { entity ->
                Reminder(
                    id = entity.id.toString(),
                    medicationId = entity.medicationId.toString(),
                    medicationName = entity.medicationName,
                    dosage = entity.dosage,
                    unit = entity.unit,
                    type = entity.type,
                    frequency = entity.frequency,
                    firstDoseTime = entity.firstDoseTime,
                    doseTime = entity.doseTime,
                    userId = entity.userId,
                    createdAt = entity.createdAt,
                    isActive = entity.isActive,
                    totalDoses = entity.totalDoses,
                    completedDoses = entity.completedDoses
                )
            }
        }
    }

    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Eliminando recordatorio: $reminderId")
            withContext(Dispatchers.IO) {
                reminderDao.deleteReminder(reminderId.toLong())
                scheduleDao.deleteSchedulesByReminderId(reminderId.toLong())
            }
            Log.d("SqliteRepository", "Recordatorio eliminado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al eliminar recordatorio", e)
            Result.failure(e)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Actualizando recordatorio: ${reminder.medicationName}")
            val entity = ReminderEntity(
                id = reminder.id.toLong(),
                medicationId = reminder.medicationId.toLongOrNull() ?: 0L,
                medicationName = reminder.medicationName,
                dosage = reminder.dosage,
                unit = reminder.unit,
                type = reminder.type,
                frequency = reminder.frequency,
                firstDoseTime = reminder.firstDoseTime,
                doseTime = reminder.doseTime,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.isActive,
                totalDoses = reminder.totalDoses,
                completedDoses = reminder.completedDoses
            )
            withContext(Dispatchers.IO) {
                reminderDao.updateReminder(entity)
            }
            Log.d("SqliteRepository", "Recordatorio actualizado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al actualizar recordatorio", e)
            Result.failure(e)
        }
    }

    // ==================== HORARIOS ====================

    suspend fun saveReminderSchedules(schedules: List<ReminderSchedule>): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Guardando ${schedules.size} horarios")
            val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
            val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(today)
            withContext(Dispatchers.IO) {
                schedules.forEach { schedule ->
                    val entity = ReminderScheduleEntity(
                        reminderId = schedule.reminderId.toLong(),
                        time = schedule.time,
                        dosage = schedule.dosage,
                        isCompleted = schedule.isCompleted,
                        completedAt = schedule.completedAt,
                        scheduledDate = todayDate!!
                    )
                    scheduleDao.insertSchedule(entity)
                }
            }
            Log.d("SqliteRepository", "Horarios guardados exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al guardar horarios", e)
            Result.failure(e)
        }
    }

    fun getTodaySchedules(userId: String): Flow<List<TodaySchedule>> {
        Log.d("SqliteRepository", "Obteniendo horarios del día para userId: $userId")
        val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(today)
        return scheduleDao.getPendingSchedulesWithMedicationName(todayDate!!).map { entities ->
            entities.map { entity ->
                TodaySchedule(
                    id = entity.id.toString(),
                    reminderId = entity.reminderId.toString(),
                    medicationName = entity.medicationName,
                    dosage = entity.dosage,
                    time = entity.time,
                    isCompleted = entity.isCompleted,
                    isOverdue = false
                )
            }
        }
    }

    suspend fun markScheduleAsCompleted(scheduleId: String): Result<Unit> {
        return try {
            Log.d("SqliteRepository", "Marcando horario como completado: $scheduleId")
            withContext(Dispatchers.IO) {
                scheduleDao.markScheduleAsCompleted(scheduleId.toLong(), Date())
            }
            Log.d("SqliteRepository", "Horario marcado como completado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al marcar horario como completado", e)
            Result.failure(e)
        }
    }

    suspend fun getDailyProgressForReminder(reminderId: String): Pair<Int, Int> {
        return try {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
            val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(today)
            val schedules = withContext(Dispatchers.IO) {
                scheduleDao.getSchedulesByReminderIdAndDate(reminderId.toLong(), todayDate!!)
            }
            val total = schedules.size
            val completed = schedules.count { it.isCompleted }
            Pair(completed, total)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo progreso diario para reminder $reminderId", e)
            Pair(0, 0)
        }
    }

    // ==================== ESTADÍSTICAS ====================

    suspend fun getMedicationStats(userId: String): Result<MedicationStats> {
        return try {
            Log.d("SqliteRepository", "Obteniendo estadísticas para userId: $userId")
            
            val statsData = withContext(Dispatchers.IO) {
                val total = medicationDao.getMedicationCount(userId)
                val active = reminderDao.getReminderCount(userId)
                val completed = scheduleDao.getCompletedSchedulesCount()
                val pending = scheduleDao.getPendingSchedulesCount()
                StatsData(total, active, completed, pending)
            }
            
            val stats = MedicationStats(
                totalMedications = statsData.totalMedications,
                activeReminders = statsData.activeReminders,
                completedToday = statsData.completedToday,
                pendingToday = statsData.pendingToday
            )
            
            Log.d("SqliteRepository", "Estadísticas obtenidas: $stats")
            Result.success(stats)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al obtener estadísticas", e)
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
            Log.d("SqliteRepository", "Creando recordatorio completo con horarios")
            
            // 1. Guardar medicamento
            val medicationId = saveMedication(medication).getOrThrow()
            Log.d("SqliteRepository", "Medicamento guardado con ID: $medicationId")
            
            // 2. Crear recordatorio
            val reminder = Reminder(
                medicationId = medicationId,
                medicationName = formData.medication,
                dosage = formData.dosage,
                unit = formData.unit,
                type = formData.type,
                frequency = formData.frequency,
                firstDoseTime = formData.firstDoseTime,
                doseTime = formData.doseTime,
                userId = userId,
                totalDoses = if (formData.frequency == "Diariamente") 2 else 1
            )
            
            val reminderId = saveReminder(reminder).getOrThrow()
            Log.d("SqliteRepository", "Recordatorio guardado con ID: $reminderId")
            
            // 3. Crear horarios
            val schedules = mutableListOf<ReminderSchedule>()
            
            // Primera dosis
            schedules.add(
                ReminderSchedule(
                    reminderId = reminderId,
                    time = formData.firstDoseTime,
                    dosage = "${formData.dosage} ${formData.unit}"
                )
            )
            
            // Segunda dosis (si es diariamente)
            if (formData.frequency == "Diariamente") {
                schedules.add(
                    ReminderSchedule(
                        reminderId = reminderId,
                        time = formData.doseTime,
                        dosage = "${formData.dosage} ${formData.unit}"
                    )
                )
            }
            
            saveReminderSchedules(schedules).getOrThrow()
            Log.d("SqliteRepository", "Horarios guardados exitosamente")
            
            Result.success(reminderId)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al crear recordatorio completo", e)
            Result.failure(e)
        }
    }
} 