package com.example.phone_medicatios.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.phone_medicatios.NotificationUtils
import com.example.phone_medicatios.data.Medication
import com.example.phone_medicatios.data.MedicationStats
import com.example.phone_medicatios.data.Reminder
import com.example.phone_medicatios.data.ReminderFormData
import com.example.phone_medicatios.data.TodaySchedule
import com.example.phone_medicatios.data.Repository
import com.example.phone_medicatios.data.FirebaseRepository
import com.example.phone_medicatios.data.HybridRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
    
data class ReminderEntity(
    var id: String = "",
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
    val active: Boolean = true            // Si está activo
)

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    // Hybrid repository (Firebase + SQLite)
    private val repository: Repository = HybridRepository(application.applicationContext)

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    private val _todaySchedules = MutableStateFlow<List<TodaySchedule>>(emptyList())
    val todaySchedules: StateFlow<List<TodaySchedule>> = _todaySchedules

    private val _stats = MutableStateFlow(MedicationStats())
    val stats: StateFlow<MedicationStats> = _stats

    private val _formData = MutableStateFlow(ReminderFormData())
    val formData: StateFlow<ReminderFormData> = _formData

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

    private val _shouldNavigateToDashboard = MutableStateFlow(false)
    val shouldNavigateToDashboard: StateFlow<Boolean> = _shouldNavigateToDashboard

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val totalMedications: Int
        get() = _medications.value.size

    init {
        // Cargar datos iniciales de forma segura
        loadInitialData()
        
        // Verificar si es un nuevo día y resetear dosis si es necesario
        checkAndResetDailyDoses()
    }

    // Cargar datos iniciales de forma segura
    private fun loadInitialData() {
        Log.d("ReminderViewModel", "=== CARGANDO DATOS INICIALES ===")
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Cargar datos iniciales usando métodos suspend
                val medications = repository.getMedications()
                val reminders = repository.getReminders()
                val todaySchedules = repository.getTodaySchedules()
                
                // Actualizar estados
                _medications.value = medications
                _reminders.value = reminders
                _todaySchedules.value = todaySchedules
                
                // Actualizar estadísticas
                updateStats(medications, reminders)
                
                Log.d("ReminderViewModel", "Datos iniciales cargados: ${medications.size} medicamentos, ${reminders.size} recordatorios")
                
                // Inicializar flujos en tiempo real después de cargar datos iniciales
                initializeRealTimeData()
                
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error cargando datos iniciales: ${e.message}", e)
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Inicializar flujos de datos en tiempo real
    private fun initializeRealTimeData() {
        Log.d("ReminderViewModel", "=== INICIANDO FLUJOS EN TIEMPO REAL ===")
        
        try {
            // Observar medicamentos en tiempo real
            viewModelScope.launch {
                try {
                    (repository as HybridRepository).getMedicationsFlow().collect { medications ->
                        Log.d("ReminderViewModel", "Medicamentos actualizados en tiempo real: ${medications.size}")
                        _medications.value = medications
                        updateStats(medications, _reminders.value)
                    }
                } catch (e: Exception) {
                    Log.e("ReminderViewModel", "Error en flujo de medicamentos: ${e.message}", e)
                }
            }
            
            // Observar recordatorios en tiempo real
            viewModelScope.launch {
                try {
                    (repository as HybridRepository).getRemindersFlow().collect { reminders ->
                        Log.d("ReminderViewModel", "Recordatorios actualizados en tiempo real: ${reminders.size}")
                        _reminders.value = reminders
                        updateStats(_medications.value, reminders)
                    }
                } catch (e: Exception) {
                    Log.e("ReminderViewModel", "Error en flujo de recordatorios: ${e.message}", e)
                }
            }
            
            // Observar horarios de hoy en tiempo real
            viewModelScope.launch {
                try {
                    (repository as HybridRepository).getTodaySchedulesFlow().collect { schedules ->
                        Log.d("ReminderViewModel", "Horarios de hoy actualizados en tiempo real: ${schedules.size}")
                        _todaySchedules.value = schedules
                    }
                } catch (e: Exception) {
                    Log.e("ReminderViewModel", "Error en flujo de horarios: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Error inicializando flujos en tiempo real: ${e.message}", e)
        }
    }
    


    // Verificar y resetear dosis al cambiar de día
    private fun checkAndResetDailyDoses() {
        viewModelScope.launch {
            try {
                val lastResetDate = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("last_dose_reset_date", "") ?: ""
                
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
                
                if (lastResetDate != today) {
                    Log.d("ReminderViewModel", "Nuevo día detectado, reseteando dosis...")
                    
                    // Resetear dosis
                    (repository as HybridRepository).resetDailyDoses()
                    
                    // Guardar fecha de reseteo
                    getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("last_dose_reset_date", today)
                        .apply()
                    
                    Log.d("ReminderViewModel", "Dosis reseteadas para el nuevo día")
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error verificando reseteo de dosis: ${e.message}")
            }
        }
    }
    
    // Función para actualizar estadísticas
    private fun updateStats(medications: List<Medication>, reminders: List<Reminder>) {
        val activeReminders = reminders.count { it.active }
        
        // Calcular dosis completadas y pendientes basadas en dosis individuales
        var totalCompletedDoses = 0
        var totalPendingDoses = 0
        
        reminders.forEach { reminder ->
            if (reminder.active) {
                val completedDoses = reminder.getCompletedDosesCount()
                val totalDoses = reminder.getTotalDosesCount()
                totalCompletedDoses += completedDoses
                totalPendingDoses += (totalDoses - completedDoses)
            }
        }
        
        val stats = MedicationStats(
            totalMedications = medications.size,
            activeReminders = activeReminders,
            completedToday = totalCompletedDoses,
            pendingToday = totalPendingDoses
        )
        
        _stats.value = stats
        Log.d("ReminderViewModel", "Estadísticas actualizadas: $stats (dosis completadas: $totalCompletedDoses, pendientes: $totalPendingDoses)")
    }

    fun updateFormData(newData: ReminderFormData) {
        _formData.value = newData
        _errorMessage.value = ""
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun resetNavigation() {
        _shouldNavigateToDashboard.value = false
    }

    fun shouldNavigateToDashboard(): Boolean {
        return _shouldNavigateToDashboard.value
    }

    fun addMedication(name: String, dosage: String, unit: String, type: String) {
        val medication = Medication(
            name = name,
            dosage = dosage,
            unit = unit,
            type = type,
            description = "", // Campo requerido pero vacío por defecto
            instructions = "", // Campo requerido pero vacío por defecto
            userId = "user1",
            createdAt = Date(),
            isActive = true
        )

        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Guardar en Firebase
                val medicationId = repository.addMedication(medication)
                
                if (medicationId != null) {
                    _successMessage.value = "¡Medicamento añadido exitosamente!"
                    
                    // Recargar datos inmediatamente
                    // loadData() // This line is removed as data is now real-time
                    
                    Log.d("ReminderViewModel", "Medicamento agregado: $name")
                } else {
                    _errorMessage.value = "Error al guardar en Firebase"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar: ${e.message}"
                Log.e("ReminderViewModel", "Error adding medication: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMedication(data: ReminderEntity, context: Context) {
        if (!validateCompleteForm(data)) return

        _isLoading.value = true
        Log.d("ReminderViewModel", "Guardando recordatorio: ${data.name}")

        viewModelScope.launch {
            try {
                // Crear el recordatorio
                val newReminder = Reminder(
                    id = "", // Firebase generará el ID
                    name = data.name,
                    type = data.type,
                    dosage = data.dosage,
                    unit = data.unit,
                    instructions = data.instructions,
                    frequencyHours = data.frequencyHours,
                    firstHour = data.firstHour,
                    days = data.days,
                    userId = "user1",
                    createdAt = data.createdAt,
                    completed = data.completed,
                    active = data.active
                )
                
                // Guardar en Firebase
                val reminderId = repository.addReminder(newReminder)
                
                if (reminderId != null) {
                    Log.d("ReminderViewModel", "Recordatorio guardado con ID: $reminderId")
                    _successMessage.value = "¡Recordatorio creado exitosamente!"
                    scheduleNotifications(context, data, reminderId)
                    _shouldNavigateToDashboard.value = true
                    
                    // Recargar datos inmediatamente
                    // loadData() // This line is removed as data is now real-time
                    
                    Log.d("ReminderViewModel", "Recordatorio agregado: ${data.name}")
                } else {
                    Log.e("ReminderViewModel", "Error: No se pudo crear el recordatorio")
                    _errorMessage.value = "Error al guardar en Firebase"
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error al guardar recordatorio: ${e.message}", e)
                _errorMessage.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveReminder(context: Context) {
        val data = _formData.value
        
        if (!validateForm()) return

        _isLoading.value = true

        val reminderData = ReminderEntity(
            name = data.name,
            type = data.type,
            dosage = data.dosage,
            unit = data.unit,
            instructions = data.instructions,
            frequencyHours = data.frequencyHours,
            firstHour = data.firstHour,
            days = data.days
        )

        addMedication(reminderData, context)
    }

    fun updateReminder(context: Context, reminderId: String) {
        val data = _formData.value
        
        if (!validateForm()) return

        _isLoading.value = true

        val reminderData = ReminderEntity(
            id = reminderId,
            name = data.name,
            type = data.type,
            dosage = data.dosage,
            unit = data.unit,
            instructions = data.instructions,
            frequencyHours = data.frequencyHours,
            firstHour = data.firstHour,
            days = data.days
        )

        updateMedication(reminderData, context)
    }

    fun deleteMedication(medicationId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Eliminar de Firebase
                val success = repository.deleteMedication(medicationId)
                
                if (success) {
                    _successMessage.value = "¡Medicamento eliminado exitosamente!"
                    
                    // Recargar datos inmediatamente
                    // loadData() // This line is removed as data is now real-time
                    
                    Log.d("ReminderViewModel", "Medicamento eliminado: $medicationId")
                } else {
                    _errorMessage.value = "Error al eliminar de Firebase"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
                Log.e("ReminderViewModel", "Error deleting medication: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedication(medicationId: String, formData: ReminderFormData) {
        val medication = Medication(
            id = medicationId,
            name = formData.name,
            dosage = formData.dosage.toString(),
            unit = formData.unit,
            type = formData.type,
            description = formData.instructions, // Usar instructions como description
            instructions = formData.instructions,
            userId = "user1",
            createdAt = Date(),
            isActive = true
        )

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val success = repository.updateMedication(medication)
                if (success) {
                    _successMessage.value = "¡Medicamento actualizado exitosamente!"
                    // loadData() // Recargar datos - This line is removed as data is now real-time
                } else {
                    _errorMessage.value = "Error al actualizar en Firebase"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedication(data: ReminderEntity, context: Context) {
        if (data.id.isBlank()) {
            _errorMessage.value = "ID del medicamento no especificado"
            return
        }

        if (!validateCompleteForm(data)) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val updatedReminder = Reminder(
                    id = data.id,
                    name = data.name,
                    type = data.type,
                    dosage = data.dosage,
                    unit = data.unit,
                    instructions = data.instructions,
                    frequencyHours = data.frequencyHours,
                    firstHour = data.firstHour,
                    days = data.days,
                    userId = data.userId,
                    createdAt = data.createdAt,
                    completed = data.completed,
                    active = data.active
                )
                
                val success = repository.updateReminder(updatedReminder)
                if (success) {
                    _successMessage.value = "¡Recordatorio actualizado!"
                    scheduleNotifications(context, data, data.id)
                    // loadData() // Recargar datos - This line is removed as data is now real-time
                } else {
                    _errorMessage.value = "Error al actualizar en Firebase"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReminder(id: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Buscar el recordatorio para obtener el objeto completo
                val reminder = _reminders.value.find { it.id == id }
                if (reminder != null) {
                    // Eliminar de Firebase
                    val success = repository.deleteReminder(reminder)
                
                if (success) {
                    _successMessage.value = "¡Recordatorio eliminado exitosamente!"
                    
                    // Recargar datos inmediatamente
                    // loadData() // This line is removed as data is now real-time
                    
                    Log.d("ReminderViewModel", "Recordatorio eliminado: $id")
                } else {
                    _errorMessage.value = "Error al eliminar de Firebase"
                }
                } else {
                    _errorMessage.value = "Recordatorio no encontrado"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
                Log.e("ReminderViewModel", "Error deleting reminder: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsCompleted(id: String) {
        viewModelScope.launch {
            try {
                // Marcar como completado en Firebase
                val success = repository.markReminderCompleted(id, true)
                
                if (success) {
                    _successMessage.value = "¡Medicamento tomado! ✅"
                    // loadData() // Recargar datos - This line is removed as data is now real-time
                } else {
                    _errorMessage.value = "Error al actualizar en Firebase"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al marcar: ${e.message}"
            }
        }
    }

    fun markScheduleAsCompleted(scheduleId: String) {
        viewModelScope.launch {
            try {
                Log.d("ReminderViewModel", "Marcando dosis como completada: $scheduleId")
                
                // Extraer el reminderId del scheduleId (formato: "reminderId_index")
                val reminderId = if (scheduleId.contains("_")) {
                    scheduleId.substringBeforeLast("_")
                } else {
                    scheduleId
                }
                
                Log.d("ReminderViewModel", "ReminderId extraído: $reminderId")
                
                // Buscar el recordatorio correspondiente
                val reminder = _reminders.value.find { it.id == reminderId }
                if (reminder != null) {
                    Log.d("ReminderViewModel", "Recordatorio encontrado: ${reminder.name}")
                    
                    // Extraer el índice de la dosis del scheduleId
                    val doseIndex = if (scheduleId.contains("_")) {
                        scheduleId.substringAfterLast("_").toIntOrNull() ?: 0
                    } else {
                        0
                    }
                    
                    Log.d("ReminderViewModel", "Índice de dosis: $doseIndex")
                    
                    // Marcar dosis específica como completada en Firebase
                    val success = repository.markDoseAsCompleted(reminderId, doseIndex)
                    
                    if (success) {
                        _successMessage.value = "¡Medicamento tomado! ✅"
                        Log.d("ReminderViewModel", "Dosis marcada como completada exitosamente")
                    } else {
                        _errorMessage.value = "Error al actualizar en Firebase"
                        Log.e("ReminderViewModel", "Error al marcar dosis como completada")
                    }
                } else {
                    _errorMessage.value = "Recordatorio no encontrado"
                    Log.e("ReminderViewModel", "Recordatorio no encontrado para ID: $reminderId")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al marcar como completado: ${e.message}"
                Log.e("ReminderViewModel", "Error marking schedule as completed: ${e.message}")
            }
        }
    }

    suspend fun getDailyProgressForReminder(reminderId: String): Pair<Int, Int> {
        val reminder = _reminders.value.find { it.id == reminderId }
        return if (reminder != null) {
            val completed = reminder.getCompletedDosesCount()
            val total = reminder.getTotalDosesCount()
            Pair(completed, total)
        } else {
            Pair(0, 0)
        }
    }

    fun validateScheduleForm(): Boolean {
        val data = _formData.value

        if (data.frequencyHours <= 0) {
            _errorMessage.value = "Por favor selecciona la frecuencia"
            return false
        }
        if (data.firstHour.isBlank()) {
            _errorMessage.value = "Por favor selecciona la primera hora"
            return false
        }
        if (data.days.isEmpty()) {
            _errorMessage.value = "Por favor selecciona al menos un día"
            return false
        }

        return true
    }

    private fun validateForm(): Boolean {
        val data = _formData.value

        if (data.name.isBlank()) {
            _errorMessage.value = "Por favor ingresa el nombre del medicamento"
            return false
        }
        if (data.type.isBlank()) {
            _errorMessage.value = "Por favor selecciona el tipo de medicamento"
            return false
        }
        if (data.dosage <= 0.0) {
            _errorMessage.value = "Por favor ingresa la dosis"
            return false
        }
        if (data.unit.isBlank()) {
            _errorMessage.value = "Por favor selecciona la unidad"
            return false
        }
        if (data.frequencyHours <= 0) {
            _errorMessage.value = "Por favor selecciona la frecuencia"
            return false
        }
        if (data.firstHour.isBlank()) {
            _errorMessage.value = "Por favor selecciona la primera hora"
            return false
        }
        if (data.days.isEmpty()) {
            _errorMessage.value = "Por favor selecciona al menos un día"
            return false
        }

        return true
    }

    private fun scheduleNotifications(context: Context, data: ReminderEntity, reminderId: String) {
        try {
            // Crear un recordatorio temporal para calcular los horarios
            val tempReminder = Reminder(
                name = data.name,
                type = data.type,
                dosage = data.dosage,
                unit = data.unit,
                instructions = data.instructions,
                frequencyHours = data.frequencyHours,
                firstHour = data.firstHour,
                days = data.days
            )
            
            // Calcular todos los horarios basados en la frecuencia
            val calculatedHours = tempReminder.calculateSchedule()
            
            // Programar notificaciones para cada horario
            calculatedHours.forEachIndexed { index, hour ->
                val doseMillis = parseTimeToMillis(hour)
                NotificationUtils.scheduleNotification(
                    context,
                    "Recordatorio de medicamento",
                    "Es hora de tomar ${data.name} (${data.dosage} ${data.unit})",
                    doseMillis,
                    (reminderId.hashCode() + index)
                )
            }
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Error al programar notificación", e)
        }
    }

    private fun parseTimeToMillis(time: String): Long {
        val calendar = Calendar.getInstance()
        val cleaned = time.replace("a.m.", "AM").replace("p.m.", "PM").replace(" ", "")
        val formats = listOf("h:mma", "hh:mma", "H:mm", "HH:mm")
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val date = sdf.parse(cleaned)
                if (date != null) {
                    val now = Calendar.getInstance()
                    calendar.time = date
                    calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    if (calendar.timeInMillis < now.timeInMillis) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    return calendar.timeInMillis
                }
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis() + 60000
    }


    
    // Validaciones
    private fun validateCompleteForm(data: ReminderEntity): Boolean {
        when {
            data.name.isBlank() -> _errorMessage.value = "Por favor ingresa el nombre del medicamento"
            data.type.isBlank() -> _errorMessage.value = "Por favor selecciona el tipo de medicamento"
            data.dosage <= 0.0 -> _errorMessage.value = "Por favor ingresa la dosis"
            data.unit.isBlank() -> _errorMessage.value = "Por favor selecciona la unidad"
            data.frequencyHours <= 0 -> _errorMessage.value = "Por favor selecciona la frecuencia"
            data.firstHour.isBlank() -> _errorMessage.value = "Por favor selecciona la primera hora"
            data.days.isEmpty() -> _errorMessage.value = "Por favor selecciona al menos un día"
            else -> return true
        }
        return false
    }
}