package com.example.phone_medicatios.data.dao

import androidx.room.*
import com.example.phone_medicatios.data.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId AND isActive = 1")
    fun getRemindersByUserId(userId: String): Flow<List<ReminderEntity>>
    
    @Query("SELECT * FROM reminders WHERE isActive = 1")
    fun getAllReminders(): List<ReminderEntity>
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): ReminderEntity?
    
    @Insert
    fun insertReminder(reminder: ReminderEntity): Long
    
    @Update
    fun updateReminder(reminder: ReminderEntity)
    
    @Query("UPDATE reminders SET id = :newId WHERE id = :oldId")
    fun updateReminderId(oldId: Long, newId: Long)
    
    @Query("UPDATE reminders SET isActive = 0 WHERE id = :reminderId")
    fun deleteReminder(reminderId: Long)
    
    @Query("UPDATE reminders SET completedDoses = :completed WHERE id = :reminderId")
    fun markReminderCompleted(reminderId: Long, completed: Boolean)
    
    @Query("UPDATE reminders SET completedDoses = :completedDoses WHERE id = :reminderId")
    fun updateReminderCompletedDoses(reminderId: Long, completedDoses: String)
    
    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId AND isActive = 1")
    fun getReminderCount(userId: String): Int
} 