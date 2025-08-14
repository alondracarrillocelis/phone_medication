package com.example.phone_medicatios.data

import java.util.Date

// Modelo para medicamentos (colección separada)
data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val unit: String = "",
    val type: String = "",
    val description: String = "",
    val instructions: String = "",
    val userId: String = "",
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
    val totalReminders: Int = 0
)

// Modelo para recordatorios - Actualizado para coincidir con la pantalla de carro
data class Reminder(
    val id: String = "",
    val name: String = "",                // Nombre del medicamento
    val type: String = "",                // Tipo (Tableta, Jarabe, etc.)
    val dosage: Double = 0.0,             // Dosis
    val unit: String = "",                // Unidad (mg, ml, etc.)
    val instructions: String = "",        // Instrucciones
    val frequencyHours: Int = 8,          // Frecuencia (cada X horas)
    val firstHour: String = "",           // Primera hora
    val days: List<String> = emptyList(), // Días seleccionados
    val userId: String = "",
    val createdAt: Date = Date(),
    val completed: Boolean = false,       // Si está completado
    val active: Boolean = true,           // Si está activo
    val completedDoses: List<Int> = emptyList() // Índices de dosis completadas
) {
    // Función para verificar si está en curso
    fun isOngoing(): Boolean {
        return active
    }

    // Función para obtener el progreso de dosis completadas
    fun getCompletedDosesCount(): Int = completedDoses.size
    
    // Función para obtener el total de dosis del día
    fun getTotalDosesCount(): Int {
        // Calcular dosis basado en frecuencia de horas
        val hoursInDay = 24
        return hoursInDay / frequencyHours
    }
    
    // Función para verificar si una dosis específica está completada
    fun isDoseCompleted(doseIndex: Int): Boolean = completedDoses.contains(doseIndex)
    
    // Función para calcular el porcentaje de progreso
    fun getProgressPercentage(): Int {
        val total = getTotalDosesCount()
        return if (total > 0) (getCompletedDosesCount() * 100) / total else 0
    }

    // Función para calcular el horario
    fun calculateSchedule(): List<String> {
        if (frequencyHours <= 0 || firstHour.isBlank()) return emptyList()

        val horas = mutableListOf<String>()

        // Convertir formato AM/PM a 24h
        val hora24h = convertTo24HourFormat(firstHour)
        if (hora24h == null) return horas

        val parts = hora24h.split(":")
        if (parts.size != 2) return horas
        
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(java.util.Calendar.MINUTE, parts[1].toInt())
        }

        val totalDoses = getTotalDosesCount()

        for (i in 0 until totalDoses) {
            horas.add(String.format("%02d:%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE)))
            calendar.add(java.util.Calendar.HOUR_OF_DAY, frequencyHours)
        }

        return horas
    }

    // Función auxiliar para convertir formato AM/PM a 24h
    private fun convertTo24HourFormat(timeAMPM: String): String? {
        return try {
            val horaMap = mapOf(
                "12:00 a.m." to "00:00", "1:00 a.m." to "01:00", "2:00 a.m." to "02:00", "3:00 a.m." to "03:00",
                "4:00 a.m." to "04:00", "5:00 a.m." to "05:00", "6:00 a.m." to "06:00", "7:00 a.m." to "07:00",
                "8:00 a.m." to "08:00", "9:00 a.m." to "09:00", "10:00 a.m." to "10:00", "11:00 a.m." to "11:00",
                "12:00 p.m." to "12:00", "1:00 p.m." to "13:00", "2:00 p.m." to "14:00", "3:00 p.m." to "15:00",
                "4:00 p.m." to "16:00", "5:00 p.m." to "17:00", "6:00 p.m." to "18:00", "7:00 p.m." to "19:00",
                "8:00 p.m." to "20:00", "9:00 p.m." to "21:00", "10:00 p.m." to "22:00", "11:00 p.m." to "23:00"
            )
            horaMap[timeAMPM]
        } catch (e: Exception) {
            null
        }
    }
}

// Modelo para horarios de recordatorios
data class ReminderSchedule(
    val id: String = "",
    val reminderId: String = "",
    val time: String = "",
    val dosage: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Date? = null,
    val scheduledDate: Date = Date()
)

// Modelo para datos del formulario - Actualizado para coincidir con la pantalla de carro
data class ReminderFormData(
    val name: String = "",                // Nombre del medicamento
    val type: String = "Tableta",         // Tipo (Tableta, Jarabe, etc.)
    val dosage: Double = 0.0,             // Dosis
    val unit: String = "mg",              // Unidad (mg, ml, etc.)
    val instructions: String = "",        // Instrucciones
    val frequencyHours: Int = 8,          // Frecuencia (cada X horas)
    val firstHour: String = "",           // Primera hora (vacía por defecto)
    val days: List<String> = emptyList()  // Días seleccionados
)

// Modelo para horarios del día
data class TodaySchedule(
    val id: String = "",
    val reminderId: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val time: String = "",
    val isCompleted: Boolean = false,
    val isOverdue: Boolean = false
)

// Modelo para estadísticas
data class MedicationStats(
    val totalMedications: Int = 0,
    val activeReminders: Int = 0,
    val completedToday: Int = 0,
    val pendingToday: Int = 0
) 