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
    val dosage: String,
    val unit: String,
    val type: String,
    val frequency: String,
    val firstDoseTime: String,
    val doseTime: String,
    val hoursBetweenDoses: String,
    val selectedDays: String,
    val cycleWeeks: String,
    val userId: String,
    val createdAt: Date,
    val isActive: Boolean = true,
    val totalDoses: Int = 0,
    val completedDoses: Int = 0
) 