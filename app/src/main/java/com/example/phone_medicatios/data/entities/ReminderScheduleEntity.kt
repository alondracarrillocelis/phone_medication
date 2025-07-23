package com.example.phone_medicatios.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminder_schedules")
data class ReminderScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reminderId: Long,
    val time: String,
    val dosage: String,
    val isCompleted: Boolean = false,
    val completedAt: Date? = null,
    val scheduledDate: Date // Nuevo campo para la fecha del horario
) 