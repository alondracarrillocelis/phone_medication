package com.example.phone_medicatios.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Date

class SimpleDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "medication_database"
        private const val DATABASE_VERSION = 1
        
        // Tabla de medicamentos
        private const val TABLE_MEDICATIONS = "medications"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DOSAGE = "dosage"
        private const val COLUMN_UNIT = "unit"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_INSTRUCTIONS = "instructions"
        private const val COLUMN_USER_ID = "userId"
        private const val COLUMN_CREATED_AT = "createdAt"
        private const val COLUMN_IS_ACTIVE = "isActive"
        
        // Tabla de recordatorios
        private const val TABLE_REMINDERS = "reminders"
        private const val COLUMN_MEDICATION_ID = "medicationId"
        private const val COLUMN_MEDICATION_NAME = "medicationName"
        private const val COLUMN_FREQUENCY = "frequency"
        private const val COLUMN_FIRST_DOSE_TIME = "firstDoseTime"
        private const val COLUMN_DOSE_TIME = "doseTime"
        private const val COLUMN_TOTAL_DOSES = "totalDoses"
        private const val COLUMN_COMPLETED_DOSES = "completedDoses"
        
        // Tabla de horarios
        private const val TABLE_SCHEDULES = "schedules"
        private const val COLUMN_REMINDER_ID = "reminderId"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_IS_COMPLETED = "isCompleted"
        private const val COLUMN_COMPLETED_AT = "completedAt"
        private const val COLUMN_SCHEDULED_DATE = "scheduledDate"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        try {
            Log.d("SimpleDatabaseHelper", "Creando base de datos...")
            
            // Crear tabla de medicamentos
            val createMedicationsTable = """
                CREATE TABLE $TABLE_MEDICATIONS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_NAME TEXT NOT NULL,
                    $COLUMN_DOSAGE TEXT NOT NULL,
                    $COLUMN_UNIT TEXT NOT NULL,
                    $COLUMN_TYPE TEXT NOT NULL,
                    $COLUMN_DESCRIPTION TEXT,
                    $COLUMN_INSTRUCTIONS TEXT,
                    $COLUMN_USER_ID TEXT NOT NULL,
                    $COLUMN_CREATED_AT INTEGER NOT NULL,
                    $COLUMN_IS_ACTIVE INTEGER DEFAULT 1
                )
            """.trimIndent()
            
            // Crear tabla de recordatorios
            val createRemindersTable = """
                CREATE TABLE $TABLE_REMINDERS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_MEDICATION_ID INTEGER,
                    $COLUMN_MEDICATION_NAME TEXT NOT NULL,
                    $COLUMN_DOSAGE TEXT NOT NULL,
                    $COLUMN_UNIT TEXT NOT NULL,
                    $COLUMN_TYPE TEXT NOT NULL,
                    $COLUMN_FREQUENCY TEXT NOT NULL,
                    $COLUMN_FIRST_DOSE_TIME TEXT NOT NULL,
                    $COLUMN_DOSE_TIME TEXT NOT NULL,
                    $COLUMN_USER_ID TEXT NOT NULL,
                    $COLUMN_CREATED_AT INTEGER NOT NULL,
                    $COLUMN_IS_ACTIVE INTEGER DEFAULT 1,
                    $COLUMN_TOTAL_DOSES INTEGER DEFAULT 0,
                    $COLUMN_COMPLETED_DOSES INTEGER DEFAULT 0
                )
            """.trimIndent()
            
            // Crear tabla de horarios
            val createSchedulesTable = """
                CREATE TABLE $TABLE_SCHEDULES (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_REMINDER_ID INTEGER NOT NULL,
                    $COLUMN_TIME TEXT NOT NULL,
                    $COLUMN_DOSAGE TEXT NOT NULL,
                    $COLUMN_IS_COMPLETED INTEGER DEFAULT 0,
                    $COLUMN_COMPLETED_AT INTEGER,
                    $COLUMN_SCHEDULED_DATE INTEGER NOT NULL
                )
            """.trimIndent()
            
            db.execSQL(createMedicationsTable)
            db.execSQL(createRemindersTable)
            db.execSQL(createSchedulesTable)
            
            Log.d("SimpleDatabaseHelper", "Base de datos creada exitosamente")
        } catch (e: Exception) {
            Log.e("SimpleDatabaseHelper", "Error al crear base de datos", e)
            throw e
        }
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("SimpleDatabaseHelper", "Actualizando base de datos de $oldVersion a $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REMINDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULES")
        onCreate(db)
    }
    
    // ==================== MEDICAMENTOS ====================
    
    fun insertMedication(medication: Medication): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, medication.name)
            put(COLUMN_DOSAGE, medication.dosage)
            put(COLUMN_UNIT, medication.unit)
            put(COLUMN_TYPE, medication.type)
            put(COLUMN_DESCRIPTION, medication.description)
            put(COLUMN_INSTRUCTIONS, medication.instructions)
            put(COLUMN_USER_ID, medication.userId)
            put(COLUMN_CREATED_AT, medication.createdAt.time)
            put(COLUMN_IS_ACTIVE, if (medication.isActive) 1 else 0)
        }
        
        val id = db.insert(TABLE_MEDICATIONS, null, values)
        db.close()
        return id
    }
    
    fun getMedications(userId: String): List<Medication> {
        val medications = mutableListOf<Medication>()
        val db = this.readableDatabase
        
        try {
            val cursor = db.query(
                TABLE_MEDICATIONS,
                null,
                "$COLUMN_USER_ID = ? AND $COLUMN_IS_ACTIVE = 1",
                arrayOf(userId),
                null,
                null,
                "$COLUMN_CREATED_AT DESC"
            )
            
            while (cursor.moveToNext()) {
                try {
                    val medication = Medication(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                        unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                        type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS)),
                        userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        createdAt = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))),
                        isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
                    )
                    medications.add(medication)
                } catch (e: Exception) {
                    Log.e("SimpleDatabaseHelper", "Error al leer medicamento del cursor", e)
                }
            }
            
            cursor.close()
        } catch (e: Exception) {
            Log.e("SimpleDatabaseHelper", "Error al consultar medicamentos", e)
        } finally {
            db.close()
        }
        
        return medications
    }
    
    fun deleteMedication(id: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_MEDICATIONS, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
        return result
    }
    
    // ==================== RECORDATORIOS ====================
    
    fun insertReminder(reminder: Reminder): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEDICATION_ID, reminder.medicationId.toLongOrNull() ?: 0)
            put(COLUMN_MEDICATION_NAME, reminder.medicationName)
            put(COLUMN_DOSAGE, reminder.dosage)
            put(COLUMN_UNIT, reminder.unit)
            put(COLUMN_TYPE, reminder.type)
            put(COLUMN_FREQUENCY, reminder.frequency)
            put(COLUMN_FIRST_DOSE_TIME, reminder.firstDoseTime)
            put(COLUMN_DOSE_TIME, reminder.doseTime)
            put(COLUMN_USER_ID, reminder.userId)
            put(COLUMN_CREATED_AT, reminder.createdAt.time)
            put(COLUMN_IS_ACTIVE, if (reminder.isActive) 1 else 0)
            put(COLUMN_TOTAL_DOSES, reminder.totalDoses)
            put(COLUMN_COMPLETED_DOSES, reminder.completedDoses)
        }
        
        val id = db.insert(TABLE_REMINDERS, null, values)
        db.close()
        return id
    }
    
    fun getReminders(userId: String): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val db = this.readableDatabase
        
        val cursor = db.query(
            TABLE_REMINDERS,
            null,
            "$COLUMN_USER_ID = ? AND $COLUMN_IS_ACTIVE = 1",
            arrayOf(userId),
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )
        
        while (cursor.moveToNext()) {
            val reminder = Reminder(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                medicationId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_ID)),
                medicationName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_NAME)),
                dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                firstDoseTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_DOSE_TIME)),
                doseTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSE_TIME)),
                userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                createdAt = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))),
                isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1,
                totalDoses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_DOSES)),
                completedDoses = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_DOSES))
            )
            reminders.add(reminder)
        }
        
        cursor.close()
        db.close()
        return reminders
    }
    
    fun deleteReminder(id: String): Int {
        val db = this.writableDatabase
        // Eliminar horarios asociados
        db.delete(TABLE_SCHEDULES, "$COLUMN_REMINDER_ID = ?", arrayOf(id))
        // Eliminar recordatorio
        val result = db.delete(TABLE_REMINDERS, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
        return result
    }
    
    // ==================== HORARIOS ====================
    
    fun insertSchedule(schedule: ReminderSchedule): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REMINDER_ID, schedule.reminderId.toLongOrNull() ?: 0)
            put(COLUMN_TIME, schedule.time)
            put(COLUMN_DOSAGE, schedule.dosage)
            put(COLUMN_IS_COMPLETED, if (schedule.isCompleted) 1 else 0)
            put(COLUMN_COMPLETED_AT, schedule.completedAt?.time)
            put(COLUMN_SCHEDULED_DATE, schedule.scheduledDate.time)
        }
        
        val id = db.insert(TABLE_SCHEDULES, null, values)
        db.close()
        return id
    }
    
    fun getPendingSchedules(): List<ReminderSchedule> {
        val schedules = mutableListOf<ReminderSchedule>()
        val db = this.readableDatabase
        
        val cursor = db.query(
            TABLE_SCHEDULES,
            null,
            "$COLUMN_IS_COMPLETED = 0",
            null,
            null,
            null,
            "$COLUMN_TIME ASC"
        )
        
        while (cursor.moveToNext()) {
            val schedule = ReminderSchedule(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                reminderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_ID)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                completedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_AT)).takeIf { it > 0 }?.let { Date(it) },
                scheduledDate = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_DATE)))
            )
            schedules.add(schedule)
        }
        
        cursor.close()
        db.close()
        return schedules
    }
    
    fun getPendingSchedulesWithMedicationName(): List<Pair<ReminderSchedule, String>> {
        val schedulesWithNames = mutableListOf<Pair<ReminderSchedule, String>>()
        val db = this.readableDatabase
        
        val query = """
            SELECT s.$COLUMN_ID, s.$COLUMN_REMINDER_ID, s.$COLUMN_TIME, s.$COLUMN_DOSAGE, 
                   s.$COLUMN_IS_COMPLETED, s.$COLUMN_COMPLETED_AT, s.$COLUMN_SCHEDULED_DATE,
                   r.$COLUMN_MEDICATION_NAME
            FROM $TABLE_SCHEDULES s
            INNER JOIN $TABLE_REMINDERS r ON s.$COLUMN_REMINDER_ID = r.$COLUMN_ID
            WHERE s.$COLUMN_IS_COMPLETED = 0
            ORDER BY s.$COLUMN_TIME ASC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, null)
        
        while (cursor.moveToNext()) {
            val schedule = ReminderSchedule(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                reminderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_ID)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                dosage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)),
                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
                completedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_AT)).takeIf { it > 0 }?.let { Date(it) },
                scheduledDate = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_DATE)))
            )
            val medicationName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_NAME))
            schedulesWithNames.add(Pair(schedule, medicationName))
        }
        
        cursor.close()
        db.close()
        return schedulesWithNames
    }
    
    fun markScheduleAsCompleted(scheduleId: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, 1)
            put(COLUMN_COMPLETED_AT, Date().time)
        }
        val result = db.update(TABLE_SCHEDULES, values, "$COLUMN_ID = ?", arrayOf(scheduleId))
        db.close()
        return result
    }
} 