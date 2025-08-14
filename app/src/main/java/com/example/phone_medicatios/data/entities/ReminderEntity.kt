package com.example.phone_medicatios.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicationId: Long,
    val medicationName: String,
    val type: String,
    val dosage: String,
    val unit: String,
    val instructions: String,
    val frequencyHours: String,
    val firstDoseTime: String,
    val selectedDays: String,
    val userId: String,
    val createdAt: Date,
    val isActive: Boolean = true,
    val totalDoses: Int = 0,
    val completedDoses: String = ""
) 
