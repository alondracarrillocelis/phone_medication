package com.example.phone_medicatios.data.dao

import androidx.room.*
import com.example.phone_medicatios.data.entities.ReminderScheduleEntity
import kotlinx.coroutines.flow.Flow

// Data class for the joined result
data class ScheduleWithMedicationName(
    val id: Long,
    val reminderId: Long,
    val time: String,
    val dosage: String,
    val isCompleted: Boolean,
    val completedAt: java.util.Date?,
    val scheduledDate: java.util.Date,
    val medicationName: String
)

@Dao
interface ReminderScheduleDao {
    @Query("SELECT * FROM reminder_schedules WHERE reminderId = :reminderId")
    fun getSchedulesByReminderId(reminderId: Long): Flow<List<ReminderScheduleEntity>>
    
    @Query("SELECT * FROM reminder_schedules WHERE isCompleted = 0")
    fun getPendingSchedules(): Flow<List<ReminderScheduleEntity>>
    
    @Query("SELECT * FROM reminder_schedules WHERE isCompleted = 0 AND scheduledDate = :today")
    fun getPendingSchedulesForDate(today: java.util.Date): Flow<List<ReminderScheduleEntity>>
    
    @Query("""
        SELECT rs.id, rs.reminderId, rs.time, rs.dosage, rs.isCompleted, 
               rs.completedAt, rs.scheduledDate, r.medicationName
        FROM reminder_schedules rs
        INNER JOIN reminders r ON rs.reminderId = r.id
        WHERE rs.isCompleted = 0 AND rs.scheduledDate = :today
    """)
    fun getPendingSchedulesWithMedicationName(today: java.util.Date): Flow<List<ScheduleWithMedicationName>>
    
    @Insert
    fun insertSchedule(schedule: ReminderScheduleEntity): Long
    
    @Update
    fun updateSchedule(schedule: ReminderScheduleEntity)
    
    @Query("UPDATE reminder_schedules SET isCompleted = 1, completedAt = :completedAt WHERE id = :scheduleId")
    fun markScheduleAsCompleted(scheduleId: Long, completedAt: java.util.Date)
    
    @Query("DELETE FROM reminder_schedules WHERE reminderId = :reminderId")
    fun deleteSchedulesByReminderId(reminderId: Long)
    
    @Query("SELECT COUNT(*) FROM reminder_schedules WHERE isCompleted = 1")
    fun getCompletedSchedulesCount(): Int
    
    @Query("SELECT COUNT(*) FROM reminder_schedules WHERE isCompleted = 0")
    fun getPendingSchedulesCount(): Int

    @Query("SELECT * FROM reminder_schedules WHERE reminderId = :reminderId AND scheduledDate = :date")
    fun getSchedulesByReminderIdAndDate(reminderId: Long, date: java.util.Date): List<ReminderScheduleEntity>
} 