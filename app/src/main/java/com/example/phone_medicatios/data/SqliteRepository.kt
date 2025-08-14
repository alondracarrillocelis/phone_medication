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

    // Métodos para el HybridRepository
    suspend fun getMedications(): List<Medication> {
        return try {
            withContext(Dispatchers.IO) {
                medicationDao.getAllMedications().map { entity ->
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
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo medicamentos: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun getMedicationById(id: String): Medication? {
        return try {
            withContext(Dispatchers.IO) {
                val entity = medicationDao.getMedicationById(id.toLong())
                entity?.let {
                    Medication(
                        id = it.id.toString(),
                        name = it.name,
                        dosage = it.dosage,
                        unit = it.unit,
                        type = it.type,
                        description = it.description,
                        instructions = it.instructions,
                        userId = it.userId,
                        createdAt = it.createdAt,
                        isActive = it.isActive,
                        totalReminders = it.totalReminders
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo medicamento por ID: ${e.message}")
            null
        }
    }
    
    suspend fun addMedication(medication: Medication): String? {
        return try {
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
            id.toString()
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error agregando medicamento: ${e.message}")
            null
        }
    }
    
    suspend fun updateMedication(medication: Medication): Boolean {
        return try {
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
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error actualizando medicamento: ${e.message}")
            false
        }
    }
    
    suspend fun updateMedicationId(oldId: String, newId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                medicationDao.updateMedicationId(oldId.toLong(), newId.toLong())
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error actualizando ID de medicamento: ${e.message}")
            false
        }
    }
    
    suspend fun deleteMedication(medicationId: String): Boolean {
        return try {
            Log.d("SqliteRepository", "Eliminando medicamento: $medicationId")
            withContext(Dispatchers.IO) {
                medicationDao.deleteMedication(medicationId.toLong())
            }
            Log.d("SqliteRepository", "Medicamento eliminado exitosamente")
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al eliminar medicamento", e)
            false
        }
    }

    // ==================== RECORDATORIOS ====================

    suspend fun saveReminder(reminder: Reminder): Result<String> {
        return try {
            Log.d("SqliteRepository", "Guardando recordatorio: ${reminder.name}")
            val entity = ReminderEntity(
                medicationId = 0L, // Valor por defecto ya que no tenemos medicationId en el nuevo modelo
                medicationName = reminder.name,
                type = reminder.type,
                dosage = reminder.dosage.toString(),
                unit = reminder.unit,
                instructions = reminder.instructions,
                frequencyHours = reminder.frequencyHours.toString(),
                firstDoseTime = reminder.firstHour,
                selectedDays = reminder.days.joinToString(","),
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.active,
                totalDoses = reminder.getTotalDosesCount(),
                completedDoses = reminder.completedDoses.joinToString(",")
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
                // Parsear las dosis completadas desde string
                val completedDosesList = entity.completedDoses.split(",")
                    .filter { it.isNotBlank() }
                    .mapNotNull { it.toIntOrNull() }
                
                Reminder(
                    id = entity.id.toString(),
                    name = entity.medicationName,
                    type = entity.type,
                    dosage = entity.dosage.toDoubleOrNull() ?: 0.0,
                    unit = entity.unit,
                    instructions = entity.instructions,
                    frequencyHours = entity.frequencyHours.toIntOrNull() ?: 8,
                    firstHour = entity.firstDoseTime,
                    days = entity.selectedDays.split(",").filter { it.isNotBlank() },
                    userId = entity.userId,
                    createdAt = entity.createdAt,
                    active = entity.isActive,
                    completedDoses = completedDosesList
                )
            }
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

    // ==================== MÉTODOS PARA HYBRID REPOSITORY ====================
    
    suspend fun getReminders(): List<Reminder> {
        return try {
                                withContext(Dispatchers.IO) {
                        reminderDao.getAllReminders().map { entity ->
                            Reminder(
                                id = entity.id.toString(),
                                name = entity.medicationName,
                                type = entity.type,
                                dosage = entity.dosage.toDoubleOrNull() ?: 0.0,
                                unit = entity.unit,
                                instructions = entity.instructions,
                                frequencyHours = entity.frequencyHours.toIntOrNull() ?: 8,
                                firstHour = entity.firstDoseTime,
                                days = entity.selectedDays.split(",").filter { it.isNotBlank() },
                                userId = entity.userId,
                                createdAt = entity.createdAt,
                                active = entity.isActive,
                                completedDoses = entity.completedDoses.split(",")
                                    .filter { it.isNotBlank() }
                                    .mapNotNull { it.toIntOrNull() }
                            )
                        }
                    }
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo recordatorios: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun getReminderById(id: String): Reminder? {
        return try {
            withContext(Dispatchers.IO) {
                val entity = reminderDao.getReminderById(id.toLong())
                entity?.let {
                    Reminder(
                        id = it.id.toString(),
                        name = it.medicationName,
                        type = it.type,
                        dosage = it.dosage.toDoubleOrNull() ?: 0.0,
                        unit = it.unit,
                        instructions = it.instructions,
                        frequencyHours = it.frequencyHours.toIntOrNull() ?: 8,
                        firstHour = it.firstDoseTime,
                        days = it.selectedDays.split(",").filter { it.isNotBlank() },
                        userId = it.userId,
                        createdAt = it.createdAt,
                        active = it.isActive,
                        completedDoses = it.completedDoses.split(",")
                            .filter { it.isNotBlank() }
                            .mapNotNull { it.toIntOrNull() }
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo recordatorio por ID: ${e.message}")
            null
        }
    }
    
    suspend fun addReminder(reminder: Reminder): String? {
        return try {
            val entity = ReminderEntity(
                medicationId = 0L, // Valor por defecto
                medicationName = reminder.name,
                type = reminder.type,
                dosage = reminder.dosage.toString(),
                unit = reminder.unit,
                instructions = reminder.instructions,
                frequencyHours = reminder.frequencyHours.toString(),
                firstDoseTime = reminder.firstHour,
                selectedDays = reminder.days.joinToString(","),
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.active,
                totalDoses = reminder.getTotalDosesCount(),
                completedDoses = reminder.completedDoses.joinToString(",")
            )
            val id = withContext(Dispatchers.IO) {
                reminderDao.insertReminder(entity)
            }
            id.toString()
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error agregando recordatorio: ${e.message}")
            null
        }
    }
    
    suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            val entity = ReminderEntity(
                id = reminder.id.toLong(),
                medicationId = 0L, // Valor por defecto
                medicationName = reminder.name,
                type = reminder.type,
                dosage = reminder.dosage.toString(),
                unit = reminder.unit,
                instructions = reminder.instructions,
                frequencyHours = reminder.frequencyHours.toString(),
                firstDoseTime = reminder.firstHour,
                selectedDays = reminder.days.joinToString(","),
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = reminder.active,
                totalDoses = reminder.getTotalDosesCount(),
                completedDoses = reminder.completedDoses.joinToString(",")
            )
            withContext(Dispatchers.IO) {
                reminderDao.updateReminder(entity)
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error actualizando recordatorio: ${e.message}")
            false
        }
    }
    
    suspend fun updateReminderId(oldId: String, newId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                reminderDao.updateReminderId(oldId.toLong(), newId.toLong())
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error actualizando ID de recordatorio: ${e.message}")
            false
        }
    }
    
    suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                reminderDao.deleteReminder(reminderId.toLong())
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error eliminando recordatorio: ${e.message}")
            false
        }
    }
    
    suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                reminderDao.markReminderCompleted(reminderId.toLong(), completed)
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error marcando recordatorio como completado: ${e.message}")
            false
        }
    }
    
    suspend fun markDoseAsCompleted(reminderId: String, doseIndex: Int): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Obtener el recordatorio actual
                val reminder = reminderDao.getReminderById(reminderId.toLong())
                if (reminder != null) {
                    // Parsear las dosis completadas actuales
                    val currentCompletedDoses = reminder.completedDoses.split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                        .toMutableList()
                    
                    // Toggle la dosis específica
                    if (currentCompletedDoses.contains(doseIndex)) {
                        currentCompletedDoses.remove(doseIndex)
                    } else {
                        currentCompletedDoses.add(doseIndex)
                    }
                    
                    // Actualizar el recordatorio con las nuevas dosis completadas
                    val updatedCompletedDoses = currentCompletedDoses.joinToString(",")
                    
                    reminderDao.updateReminderCompletedDoses(
                        reminderId.toLong(),
                        updatedCompletedDoses
                    )
                    
                    Log.d("SqliteRepository", "Dosis $doseIndex marcada como completada para recordatorio $reminderId")
                }
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error marcando dosis como completada: ${e.message}")
            false
        }
    }
    
    suspend fun getTodaySchedules(): List<TodaySchedule> {
        return try {
            withContext(Dispatchers.IO) {
                val today = Date()
                scheduleDao.getTodaySchedules(today).map { entity ->
                    TodaySchedule(
                        id = entity.id.toString(),
                        reminderId = entity.reminderId.toString(),
                        medicationName = "", // TODO: Obtener nombre del medicamento desde el reminder
                        dosage = entity.dosage,
                        time = entity.time,
                        isCompleted = entity.isCompleted,
                        isOverdue = false // TODO: Implementar lógica de atraso
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error obteniendo horarios de hoy: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun addOrUpdateTodaySchedule(schedule: TodaySchedule): Boolean {
        return try {
            val entity = ReminderScheduleEntity(
                reminderId = schedule.reminderId.toLong(),
                dosage = schedule.dosage,
                time = schedule.time,
                isCompleted = schedule.isCompleted,
                scheduledDate = Date()
            )
            withContext(Dispatchers.IO) {
                scheduleDao.insertSchedule(entity)
            }
            true
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error agregando/actualizando horario: ${e.message}")
            false
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
            
            val reminderId = saveReminder(reminder).getOrThrow()
            Log.d("SqliteRepository", "Recordatorio guardado con ID: $reminderId")
            
            // 3. Crear horarios
            val schedules = mutableListOf<ReminderSchedule>()
            
            // Primera dosis
            schedules.add(
                ReminderSchedule(
                    reminderId = reminderId,
                    time = formData.firstHour,
                    dosage = "${formData.dosage} ${formData.unit}"
                )
            )
            
            // Calcular horarios basados en frecuencia
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
            
            saveReminderSchedules(schedules).getOrThrow()
            Log.d("SqliteRepository", "Horarios guardados exitosamente")
            
            Result.success(reminderId)
        } catch (e: Exception) {
            Log.e("SqliteRepository", "Error al crear recordatorio completo", e)
            Result.failure(e)
        }
    }
} 