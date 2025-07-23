package com.example.phone_medicatios.data.dao

import androidx.room.*
import com.example.phone_medicatios.data.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId AND isActive = 1")
    fun getRemindersByUserId(userId: String): Flow<List<ReminderEntity>>
    
    @Insert
    fun insertReminder(reminder: ReminderEntity): Long
    
    @Update
    fun updateReminder(reminder: ReminderEntity)
    
    @Query("UPDATE reminders SET isActive = 0 WHERE id = :reminderId")
    fun deleteReminder(reminderId: Long)
    
    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId AND isActive = 1")
    fun getReminderCount(userId: String): Int
} 