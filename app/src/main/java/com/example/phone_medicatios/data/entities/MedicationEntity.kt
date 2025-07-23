package com.example.phone_medicatios.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val unit: String,
    val type: String,
    val description: String,
    val instructions: String,
    val userId: String,
    val createdAt: Date,
    val isActive: Boolean = true,
    val totalReminders: Int = 0
) 