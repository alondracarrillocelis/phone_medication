package com.example.phone_medicatios.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.phone_medicatios.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import android.content.Context
import com.example.phone_medicatios.NotificationUtils
import java.text.SimpleDateFormat

class ReminderViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {
    // Usar SqliteRepository para persistencia con Room
    private val repository: SqliteRepository
    
    init {
        try {
            Log.d("ReminderViewModel", "Inicializando ReminderViewModel...")
            repository = SqliteRepository(application.applicationContext)
            Log.d("ReminderViewModel", "ReminderViewModel inicializado exitosamente")
        } catch (e: Exception) {
            Log.e("ReminderViewModel", "Error al inicializar ReminderViewModel", e)
            throw e
        }
    }
    
    // Estados
    private val _formData = MutableStateFlow(ReminderFormData())
    val formData: StateFlow<ReminderFormData> = _formData.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()
    
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()
    
    private val _todaySchedules = MutableStateFlow<List<TodaySchedule>>(emptyList())
    val todaySchedules: StateFlow<List<TodaySchedule>> = _todaySchedules.asStateFlow()

    private val _shouldNavigateToDashboard = MutableStateFlow(false)
    val shouldNavigateToDashboard: StateFlow<Boolean> = _shouldNavigateToDashboard.asStateFlow()

    private val _stats = MutableStateFlow(MedicationStats())
    val stats: StateFlow<MedicationStats> = _stats.asStateFlow()

    // ID de usuario temporal (en una app real usarías autenticación local)
    // Usar un ID fijo para que los datos persistan entre navegaciones
    private val userId = "user_default"

    init {
        Log.d("ReminderViewModel", "Inicializando ViewModel con userId: $userId")
        startDataObservers()
    }

    fun updateFormData(newData: ReminderFormData) {
        Log.d("ReminderViewModel", "Actualizando formData:")
        Log.d("ReminderViewModel", "- Medicamento: '${newData.medication}'")
        Log.d("ReminderViewModel", "- Dosis: '${newData.dosage}'")
        Log.d("ReminderViewModel", "- Primera dosis: '${newData.firstDoseTime}'")
        Log.d("ReminderViewModel", "- Hora dosis: '${newData.doseTime}'")
        
        _formData.value = newData
        // Limpiar errores cuando el usuario modifica el formulario
        _errorMessage.value = null
    }

    fun validateForm(): Boolean {
        val data = _formData.value
        if (data.medication.isBlank()) {
            _errorMessage.value = "Por favor ingresa el nombre del medicamento"
            return false
        }
        if (data.dosage.isBlank()) {
            _errorMessage.value = "Por favor ingresa la dosis"
            return false
        }
        if (data.unit.isBlank()) {
            _errorMessage.value = "Por favor selecciona la unidad"
            return false
        }
        if (data.type.isBlank()) {
            _errorMessage.value = "Por favor selecciona el tipo de medicamento"
            return false
        }
        return true
    }

    fun validateScheduleForm(): Boolean {
        val data = _formData.value
        
        Log.d("ReminderViewModel", "Validando formulario de horarios:")
        Log.d("ReminderViewModel", "- Primera dosis: '${data.firstDoseTime}'")
        Log.d("ReminderViewModel", "- Hora dosis: '${data.doseTime}'")
        
        if (data.firstDoseTime.isBlank()) {
            Log.d("ReminderViewModel", "Error: Primera dosis está vacía")
            _errorMessage.value = "Por favor selecciona la hora de la primera dosis"
            return false
        }
        
        if (data.doseTime.isBlank()) {
            Log.d("ReminderViewModel", "Error: Hora dosis está vacía")
            _errorMessage.value = "Por favor ingresa la hora de la dosis"
            return false
        }
        
        Log.d("ReminderViewModel", "Validación de horarios exitosa")
        return true
    }

    fun validateCompleteForm(): Boolean {
        val data = _formData.value
        if (data.medication.isBlank()) {
            _errorMessage.value = "Por favor ingresa el nombre del medicamento"
            return false
        }
        if (data.dosage.isBlank()) {
            _errorMessage.value = "Por favor ingresa la dosis"
            return false
        }
        if (data.unit.isBlank()) {
            _errorMessage.value = "Por favor selecciona la unidad"
            return false
        }
        if (data.type.isBlank()) {
            _errorMessage.value = "Por favor selecciona el tipo de medicamento"
            return false
        }
        if (data.frequency.isBlank()) {
            _errorMessage.value = "Por favor selecciona la frecuencia"
            return false
        }
        if (data.firstDoseTime.isBlank()) {
            _errorMessage.value = "Por favor selecciona la hora de la primera dosis"
            return false
        }
        if (data.doseTime.isBlank()) {
            _errorMessage.value = "Por favor ingresa la hora de la dosis"
            return false
        }
        // Validar que no sean los valores por defecto
        if (data.medication == "") {
            _errorMessage.value = "El nombre del medicamento no puede estar vacío"
            return false
        }
        if (data.dosage == "") {
            _errorMessage.value = "La dosis no puede estar vacía"
            return false
        }
        return true
    }

    fun saveReminder(context: Context) {
        Log.d("ReminderViewModel", "[saveReminder] Iniciando guardado de recordatorio")
        if (!validateCompleteForm()) {
            Log.d("ReminderViewModel", "[saveReminder] Validación de formulario fallida")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                Log.d("ReminderViewModel", "[saveReminder] Creando objeto Medication")
                val medication = Medication(
                    name = _formData.value.medication,
                    dosage = _formData.value.dosage,
                    unit = _formData.value.unit,
                    type = _formData.value.type,
                    description = _formData.value.description,
                    instructions = _formData.value.instructions,
                    userId = userId
                )
                Log.d("ReminderViewModel", "[saveReminder] Llamando a repository.createReminderWithSchedules")
                val result = repository.createReminderWithSchedules(
                    medication = medication,
                    formData = _formData.value,
                    userId = userId
                )
                result.fold(
                    onSuccess = { reminderId ->
                        Log.d("ReminderViewModel", "[saveReminder] Recordatorio creado con ID: $reminderId")
                        Log.d("ReminderViewModel", "[saveReminder] userId usado: $userId")
                        _successMessage.value = "¡Recordatorio creado exitosamente! 🎉"
                        // Programar notificaciones para las horas configuradas
                        try {
                            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val today = Calendar.getInstance()
                            val firstDoseMillis = parseTimeToMillis(_formData.value.firstDoseTime)
                            Log.d("ReminderViewModel", "[saveReminder] Programando notificación para la primera dosis en: $firstDoseMillis")
                            NotificationUtils.scheduleNotification(
                                context,
                                "Recordatorio de medicamento",
                                "Es hora de tomar ${_formData.value.medication} (${_formData.value.dosage} ${_formData.value.unit})",
                                firstDoseMillis,
                                reminderId.hashCode()
                            )
                            if (_formData.value.frequency == "Diariamente") {
                                val secondDoseMillis = parseTimeToMillis(_formData.value.doseTime)
                                Log.d("ReminderViewModel", "[saveReminder] Programando notificación para la segunda dosis en: $secondDoseMillis")
                                NotificationUtils.scheduleNotification(
                                    context,
                                    "Recordatorio de medicamento",
                                    "Es hora de tomar ${_formData.value.medication} (${_formData.value.dosage} ${_formData.value.unit})",
                                    secondDoseMillis,
                                    (reminderId.hashCode() + 1)
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("ReminderViewModel", "[saveReminder] Error programando notificaciones", e)
                        }
                        Log.d("ReminderViewModel", "[saveReminder] Llamando a refreshData()")
                        refreshData()
                        Log.d("ReminderViewModel", "[saveReminder] Llamando a resetForm()")
                        resetForm()
                        Log.d("ReminderViewModel", "[saveReminder] Configurando navegación al dashboard")
                        _shouldNavigateToDashboard.value = true
                    },
                    onFailure = { exception ->
                        Log.e("ReminderViewModel", "[saveReminder] Error al crear recordatorio", exception)
                        _errorMessage.value = "Error al crear recordatorio: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "[saveReminder] Error inesperado", e)
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                Log.d("ReminderViewModel", "[saveReminder] Finalizando guardado")
                _isLoading.value = false
            }
        }
    }

    private fun parseTimeToMillis(time: String): Long {
        // Espera formatos como "8:00 a.m." o "20:00"
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
                    // Si la hora ya pasó hoy, programa para mañana
                    if (calendar.timeInMillis < now.timeInMillis) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    return calendar.timeInMillis
                }
            } catch (_: Exception) {}
        }
        // Si falla, programa para 1 minuto después
        return System.currentTimeMillis() + 60000
    }

    fun markScheduleAsCompleted(scheduleId: String) {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Marcando horario como completado: $scheduleId")
            val result = repository.markScheduleAsCompleted(scheduleId)
            result.fold(
                onSuccess = {
                    Log.d("ReminderViewModel", "Horario marcado como completado exitosamente")
                    _successMessage.value = "¡Medicamento tomado! ✅"
                    // Recargar datos para actualizar la UI
                    loadTodaySchedules()
                    loadStats()
                },
                onFailure = { exception ->
                    Log.e("ReminderViewModel", "Error al marcar horario como completado", exception)
                    _errorMessage.value = "Error al marcar como completado: ${exception.message}"
                }
            )
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Eliminando recordatorio: $reminderId")
            val result = repository.deleteReminder(reminderId)
            result.fold(
                onSuccess = {
                    Log.d("ReminderViewModel", "Recordatorio eliminado exitosamente")
                    _successMessage.value = "Recordatorio eliminado exitosamente"
                    // Recargar todos los datos para actualizar la UI
                    loadReminders()
                    loadTodaySchedules()
                    loadStats()
                },
                onFailure = { exception ->
                    Log.e("ReminderViewModel", "Error al eliminar recordatorio", exception)
                    _errorMessage.value = "Error al eliminar recordatorio: ${exception.message}"
                }
            )
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            val result = repository.deleteMedication(medicationId)
            result.fold(
                onSuccess = {
                    loadMedications()
                    loadStats()
                    _successMessage.value = "Medicamento eliminado exitosamente"
                },
                onFailure = { exception ->
                    _errorMessage.value = "Error al eliminar medicamento: ${exception.message}"
                }
            )
        }
    }

    fun updateMedication(medicationId: String, formData: ReminderFormData) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val medication = Medication(
                    id = medicationId,
                    name = formData.medication,
                    dosage = formData.dosage,
                    unit = formData.unit,
                    type = formData.type,
                    description = formData.description,
                    instructions = formData.instructions,
                    userId = userId
                )
                val result = repository.updateMedication(medication)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Medicamento actualizado exitosamente"
                        refreshData()
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al actualizar medicamento: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReminder(context: Context, reminderId: String, formData: ReminderFormData) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val reminder = Reminder(
                    id = reminderId,
                    medicationId = "", // Si tienes el id del medicamento, colócalo aquí
                    medicationName = formData.medication,
                    dosage = formData.dosage,
                    unit = formData.unit,
                    type = formData.type,
                    frequency = formData.frequency,
                    firstDoseTime = formData.firstDoseTime,
                    doseTime = formData.doseTime,
                    userId = userId
                )
                val result = repository.updateReminder(reminder)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Recordatorio actualizado exitosamente"
                        // Programar notificaciones para las horas configuradas
                        val firstDoseMillis = parseTimeToMillis(formData.firstDoseTime)
                        NotificationUtils.scheduleNotification(
                            context,
                            "Recordatorio de medicamento",
                            "Es hora de tomar ${formData.medication} (${formData.dosage} ${formData.unit})",
                            firstDoseMillis,
                            reminderId.hashCode()
                        )
                        if (formData.frequency == "Diariamente") {
                            val secondDoseMillis = parseTimeToMillis(formData.doseTime)
                            NotificationUtils.scheduleNotification(
                                context,
                                "Recordatorio de medicamento",
                                "Es hora de tomar ${formData.medication} (${formData.dosage} ${formData.unit})",
                                secondDoseMillis,
                                (reminderId.hashCode() + 1)
                            )
                        }
                        refreshData()
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al actualizar recordatorio: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMedications() {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Cargando medicamentos...")
            try {
                repository.getMedications(userId).collect { medications ->
                    Log.d("ReminderViewModel", "Medicamentos cargados: ${medications.size}")
                    _medications.value = medications
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error al cargar medicamentos", e)
                _errorMessage.value = "Error al cargar medicamentos: ${e.message}"
            }
        }
    }

    private fun loadReminders() {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Cargando recordatorios para userId: $userId")
            try {
                repository.getReminders(userId).collect { reminders ->
                    Log.d("ReminderViewModel", "Recordatorios cargados: ${reminders.size}")
                    reminders.forEach { reminder ->
                        Log.d("ReminderViewModel", "Recordatorio: ${reminder.medicationName} - ${reminder.dosage} ${reminder.unit}")
                    }
                    _reminders.value = reminders
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error al cargar recordatorios", e)
                _errorMessage.value = "Error al cargar recordatorios: ${e.message}"
            }
        }
    }

    private fun loadTodaySchedules() {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Cargando horarios del día...")
            try {
                repository.getTodaySchedules(userId).collect { schedules ->
                    Log.d("ReminderViewModel", "Horarios del día cargados: ${schedules.size}")
                    _todaySchedules.value = schedules
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error al cargar horarios del día", e)
                _errorMessage.value = "Error al cargar horarios: ${e.message}"
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            Log.d("ReminderViewModel", "Cargando estadísticas...")
            try {
                val result = repository.getMedicationStats(userId)
                result.fold(
                    onSuccess = { stats ->
                        Log.d("ReminderViewModel", "Estadísticas cargadas: $stats")
                        _stats.value = stats
                    },
                    onFailure = { exception ->
                        Log.e("ReminderViewModel", "Error al cargar estadísticas", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error al cargar estadísticas", e)
            }
        }
    }

    private fun resetForm() {
        _formData.value = ReminderFormData()
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
    
    fun resetNavigation() {
        _shouldNavigateToDashboard.value = false
    }
    
    fun refreshData() {
        // Recargar recordatorios, horarios y estadísticas
        Log.d("ReminderViewModel", "Refrescando datos...")
        Log.d("ReminderViewModel", "Llamando a loadReminders()")
        loadReminders()
        Log.d("ReminderViewModel", "Llamando a loadTodaySchedules()")
        loadTodaySchedules()
        Log.d("ReminderViewModel", "Llamando a loadStats()")
        loadStats()
        Log.d("ReminderViewModel", "RefreshData completado")
    }
    
    // Método para debug - forzar recarga de recordatorios
    fun debugLoadReminders() {
        Log.d("ReminderViewModel", "Debug: Forzando recarga de recordatorios")
        loadReminders()
    }
    
    // Método para debug - agregar datos de prueba
    fun addTestData() {
        Log.d("ReminderViewModel", "Debug: Agregando datos de prueba")
        viewModelScope.launch {
            val testReminder = Reminder(
                medicationId = "test_med_2",
                medicationName = "Ibuprofeno",
                dosage = "400",
                unit = "mg",
                type = "Tableta",
                frequency = "Diariamente",
                firstDoseTime = "9:00 a.m.",
                doseTime = "9:00 p.m.",
                userId = userId,
                totalDoses = 2,
                completedDoses = 0
            )
            val result = repository.saveReminder(testReminder)
            result.fold(
                onSuccess = { id ->
                    Log.d("ReminderViewModel", "Debug: Recordatorio de prueba agregado con ID: $id")
                    loadReminders()
                },
                onFailure = { exception ->
                    Log.e("ReminderViewModel", "Debug: Error al agregar recordatorio de prueba", exception)
                }
            )
        }
    }
    
    // Método para debug - verificar estado del repositorio
    fun debugRepositoryState() {
        Log.d("ReminderViewModel", "Debug: Verificando estado del repositorio")
        Log.d("ReminderViewModel", "Debug: userId actual: $userId")
        viewModelScope.launch {
            repository.getReminders(userId).collect { reminders ->
                Log.d("ReminderViewModel", "Debug: Recordatorios en repositorio: ${reminders.size}")
                reminders.forEach { reminder ->
                    Log.d("ReminderViewModel", "Debug: - ${reminder.medicationName} (${reminder.id})")
                }
            }
            // También verificar horarios del día
            repository.getTodaySchedules(userId).collect { schedules ->
                Log.d("ReminderViewModel", "Debug: Horarios del día: ${schedules.size}")
                schedules.forEach { schedule ->
                    Log.d("ReminderViewModel", "Debug: - ${schedule.medicationName} a las ${schedule.time} (${schedule.id})")
                }
            }
        }
    }

    fun addMedication(name: String, dosage: String, unit: String, type: String) {
        viewModelScope.launch {
            val medication = com.example.phone_medicatios.data.Medication(
                name = name,
                dosage = dosage,
                unit = unit,
                type = type,
                userId = userId,
                createdAt = java.util.Date(),
                isActive = true
            )
            val result = repository.saveMedication(medication)
            result.fold(
                onSuccess = {
                    loadMedications()
                    loadStats()
                    _successMessage.value = "Medicamento añadido exitosamente"
                },
                onFailure = { exception ->
                    _errorMessage.value = "Error al añadir medicamento: ${exception.message}"
                }
            )
        }
    }

    suspend fun getDailyProgressForReminder(reminderId: String): Pair<Int, Int> {
        return repository.getDailyProgressForReminder(reminderId)
    }

    private fun startDataObservers() {
        // Cargar datos iniciales
        loadMedications()
        loadReminders()
        loadTodaySchedules()
        loadStats()
    }
} 