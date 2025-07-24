package com.example.phone_medicatios.data.dao

import androidx.room.*
import com.example.phone_medicatios.data.entities.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE userId = :userId AND isActive = 1")
    fun getMedicationsByUserId(userId: String): Flow<List<MedicationEntity>>
    
    @Insert
    fun insertMedication(medication: MedicationEntity): Long
    
    @Update
    fun updateMedication(medication: MedicationEntity)
    
    @Query("UPDATE medications SET isActive = 0 WHERE id = :medicationId")
    fun deleteMedication(medicationId: Long)
    
    @Query("SELECT COUNT(*) FROM medications WHERE userId = :userId AND isActive = 1")
    fun getMedicationCount(userId: String): Int
} 