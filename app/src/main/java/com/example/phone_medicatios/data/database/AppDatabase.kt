package com.example.phone_medicatios.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.phone_medicatios.data.dao.MedicationDao
import com.example.phone_medicatios.data.dao.ReminderDao
import com.example.phone_medicatios.data.dao.ReminderScheduleDao
import com.example.phone_medicatios.data.database.converters.DateConverter
import com.example.phone_medicatios.data.entities.MedicationEntity
import com.example.phone_medicatios.data.entities.ReminderEntity
import com.example.phone_medicatios.data.entities.ReminderScheduleEntity

@Database(
    entities = [
        MedicationEntity::class,
        ReminderEntity::class,
        ReminderScheduleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun reminderDao(): ReminderDao
    abstract fun reminderScheduleDao(): ReminderScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medication_database"
                )
                .fallbackToDestructiveMigration() // Permite borrar y recrear la BD si cambia el esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 