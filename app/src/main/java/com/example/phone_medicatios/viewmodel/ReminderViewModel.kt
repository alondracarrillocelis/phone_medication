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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ReminderEntity(
    var id: String = "",
    val name: String = "",
    val dosage: String = "",
    val unit: String = "",
    val type: String = "",
    val description: String = "",
    val instructions: String = "",
    val frequency: String = "",
    val hour: String = "", // primera dosis
    val secondHour: String = "", // segunda dosis opcional
    val days: List<String> = emptyList(),
    val cycleWeeks: String = "",
    val userId: String = "",
    val createdAt: Date = Date(),
    val completed: Boolean = false // para marcar que fue tomado
)

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase repository
    private val repository: Repository = FirebaseRepository()

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
        loadInitialData()
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    fun resetNavigation() {
        _shouldNavigateToDashboard.value = false
    }

    fun refreshData() {
        loadInitialData()
    }

    fun updateFormData(newData: ReminderFormData) {
        _formData.value = newData
        _errorMessage.value = ""
    }

    fun loadMedications() {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Cargar datos desde Firebase
                val medications = repository.getMedications()
                val reminders = repository.getReminders()
                val todaySchedules = repository.getTodaySchedules()
                val stats = repository.getUserStats()
                
                _medications.value = medications
                _reminders.value = reminders
                _todaySchedules.value = todaySchedules
                _stats.value = stats
                
                Log.d("ReminderViewModel", "Datos cargados desde Firebase: ${medications.size} medicamentos, ${reminders.size} recordatorios")
                
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error cargando datos desde Firebase: ${e.message}")
                _errorMessage.value = "Error al cargar datos: ${e.message}"
                
                // Cargar datos de muestra como fallback
                loadSampleData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadSampleData() {
        // Load sample data for testing when Firebase is not available
        val sampleReminders = listOf(
            ReminderEntity(
                id = "1",
                name = "Paracetamol",
                dosage = "500",
                unit = "mg",
                type = "Tableta",
                frequency = "Diariamente",
                hour = "8:00 a.m.",
                secondHour = "8:00 p.m.",
                userId = "user1",
                completed = false
            ),
            ReminderEntity(
                id = "2",
                name = "Ibuprofeno",
                dosage = "400",
                unit = "mg",
                type = "Tableta",
                frequency = "Diariamente",
                hour = "9:00 a.m.",
                secondHour = "9:00 p.m.",
                userId = "user1",
                completed = true
            )
        )

        // Convert to different data types
        val medicationList = sampleReminders.map { reminder ->
            Medication(
                id = reminder.id,
                name = reminder.name,
                dosage = reminder.dosage,
                unit = reminder.unit,
                type = reminder.type,
                description = reminder.description,
                instructions = reminder.instructions,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = !reminder.completed
            )
        }
        _medications.value = medicationList

        val reminderList = sampleReminders.map { reminder ->
            Reminder(
                id = reminder.id,
                medicationId = reminder.id,
                medicationName = reminder.name,
                dosage = reminder.dosage,
                unit = reminder.unit,
                type = reminder.type,
                frequency = reminder.frequency,
                firstDoseTime = reminder.hour,
                doseTime = reminder.secondHour,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = !reminder.completed,
                totalDoses = if (reminder.frequency == "Diariamente") 2 else 1,
                completedDoses = if (reminder.completed) 1 else 0
            )
        }
        _reminders.value = reminderList

        val todaySchedulesList = sampleReminders.map { reminder ->
            TodaySchedule(
                id = reminder.id,
                reminderId = reminder.id,
                medicationName = reminder.name,
                dosage = reminder.dosage,
                time = reminder.hour,
                isCompleted = reminder.completed,
                isOverdue = false
            )
        }
        _todaySchedules.value = todaySchedulesList

        // Update stats
        val completedToday = sampleReminders.count { it.completed }
        val pendingToday = sampleReminders.size - completedToday

        _stats.value = MedicationStats(
            totalMedications = sampleReminders.size,
            activeReminders = sampleReminders.size,
            completedToday = completedToday,
            pendingToday = pendingToday
        )
    }

    fun addMedication(name: String, dosage: String, unit: String, type: String) {
        val medication = Medication(
            name = name,
            dosage = dosage,
            unit = unit,
            type = type,
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
                    loadInitialData() // Refresh all data from Firebase
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

        viewModelScope.launch {
            try {
                // Crear el recordatorio
                val newReminder = Reminder(
                    id = "", // Firebase generará el ID
                    medicationId = "", // Se generará después
                    medicationName = data.name,
                    dosage = data.dosage,
                    unit = data.unit,
                    type = data.type,
                    frequency = data.frequency,
                    firstDoseTime = data.hour,
                    doseTime = data.secondHour,
                    userId = data.userId,
                    createdAt = data.createdAt,
                    isActive = !data.completed,
                    totalDoses = if (data.frequency == "Diariamente") 2 else 1,
                    completedDoses = if (data.completed) 1 else 0
                )
                
                // Guardar en Firebase
                val reminderId = repository.addReminder(newReminder)
                
                if (reminderId != null) {
                    _successMessage.value = "¡Recordatorio creado exitosamente!"
                    scheduleNotifications(context, data, reminderId)
                    _shouldNavigateToDashboard.value = true
                    loadInitialData() // Refresh all data from Firebase
                } else {
                    _errorMessage.value = "Error al guardar en Firebase"
                }
            } catch (e: Exception) {
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
            name = data.medication,
            dosage = data.dosage,
            unit = data.unit,
            type = data.type,
            frequency = data.frequency,
            hour = data.firstDoseTime,
            secondHour = data.doseTime,
            description = data.description,
            instructions = data.instructions,
            cycleWeeks = data.cycleWeeks,
            days = data.selectedDays
        )

        addMedication(reminderData, context)
    }

    fun updateReminder(context: Context, reminderId: String) {
        val data = _formData.value
        
        if (!validateForm()) return

        _isLoading.value = true

        val reminderData = ReminderEntity(
            id = reminderId,
            name = data.medication,
            dosage = data.dosage,
            unit = data.unit,
            type = data.type,
            frequency = data.frequency,
            hour = data.firstDoseTime,
            secondHour = data.doseTime,
            description = data.description,
            instructions = data.instructions,
            cycleWeeks = data.cycleWeeks,
            days = data.selectedDays
        )

        updateMedication(reminderData, context)
    }

    fun updateMedication(medicationId: String, formData: ReminderFormData) {
        val medication = Medication(
            id = medicationId,
            name = formData.medication,
            dosage = formData.dosage,
            unit = formData.unit,
            type = formData.type,
            description = formData.description,
            instructions = formData.instructions,
            userId = "user1",
            createdAt = Date(),
            isActive = true
        )

        _isLoading.value = true

        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(500) // Simulate network delay
                val currentList = _medications.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == medicationId }
                if (index != -1) {
                    currentList[index] = medication
                    _medications.value = currentList
                    _successMessage.value = "¡Medicamento actualizado exitosamente!"
                    loadInitialData() // Refresh all data
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
                kotlinx.coroutines.delay(500) // Simulate network delay
                
                val currentReminders = _reminders.value.toMutableList()
                val index = currentReminders.indexOfFirst { it.id == data.id }
                if (index != -1) {
                    val updatedReminder = Reminder(
                        id = data.id,
                        medicationId = data.id,
                        medicationName = data.name,
                        dosage = data.dosage,
                        unit = data.unit,
                        type = data.type,
                        frequency = data.frequency,
                        firstDoseTime = data.hour,
                        doseTime = data.secondHour,
                        userId = data.userId,
                        createdAt = data.createdAt,
                        isActive = !data.completed,
                        totalDoses = if (data.frequency == "Diariamente") 2 else 1,
                        completedDoses = if (data.completed) 1 else 0
                    )
                    currentReminders[index] = updatedReminder
                    _reminders.value = currentReminders
                    _successMessage.value = "¡Recordatorio actualizado!"
                    scheduleNotifications(context, data, data.id)
                    loadInitialData() // Refresh all data
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMedication(id: String) {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(300) // Simulate network delay
                val currentList = _medications.value.toMutableList()
                currentList.removeAll { it.id == id }
                _medications.value = currentList
                _successMessage.value = "Medicamento eliminado exitosamente"
                loadInitialData() // Refresh all data
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(300) // Simulate network delay
                val currentList = _reminders.value.toMutableList()
                currentList.removeAll { it.id == id }
                _reminders.value = currentList
                _successMessage.value = "Recordatorio eliminado exitosamente"
                loadInitialData() // Refresh all data
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun markAsCompleted(id: String) {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(300) // Simulate network delay
                val currentList = _reminders.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(completedDoses = 1)
                    _reminders.value = currentList
                    _successMessage.value = "¡Medicamento tomado! ✅"
                    loadInitialData() // Refresh all data
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al marcar: ${e.message}"
            }
        }
    }

    fun markScheduleAsCompleted(scheduleId: String) {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(300) // Simulate network delay
                val currentList = _reminders.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == scheduleId }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(completedDoses = 1)
                    _reminders.value = currentList
                    _successMessage.value = "¡Medicamento tomado! ✅"
                    loadInitialData() // Refresh all data
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al marcar como completado: ${e.message}"
            }
        }
    }

    suspend fun getDailyProgressForReminder(reminderId: String): Pair<Int, Int> {
        val reminder = _reminders.value.find { it.id == reminderId }
        return if (reminder != null) {
            Pair(reminder.completedDoses, reminder.totalDoses)
        } else {
            Pair(0, 0)
        }
    }

    fun validateScheduleForm(): Boolean {
        val data = _formData.value

        if (data.frequency.isBlank()) {
            _errorMessage.value = "Por favor selecciona la frecuencia"
            return false
        }
        if (data.firstDoseTime.isBlank()) {
            _errorMessage.value = "Por favor selecciona la primera dosis"
            return false
        }
        if (data.doseTime.isBlank()) {
            _errorMessage.value = "Por favor ingresa la hora de la segunda dosis"
            return false
        }
        if (data.frequency == "Días Seleccionados" && data.selectedDays.isEmpty()) {
            _errorMessage.value = "Por favor selecciona al menos un día"
            return false
        }
        if (data.frequency == "Cíclicamente" && data.cycleWeeks.isBlank()) {
            _errorMessage.value = "Por favor selecciona el ciclo de semanas"
            return false
        }

        return true
    }

    private fun validateForm(): Boolean {
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
            _errorMessage.value = "Por favor ingresa la hora de la segunda dosis"
            return false
        }
        if (data.frequency == "Días Seleccionados" && data.selectedDays.isEmpty()) {
            _errorMessage.value = "Por favor selecciona al menos un día"
            return false
        }
        if (data.frequency == "Cíclicamente" && data.cycleWeeks.isBlank()) {
            _errorMessage.value = "Por favor selecciona el ciclo de semanas"
            return false
        }

        return true
    }

    private fun scheduleNotifications(context: Context, data: ReminderEntity, reminderId: String) {
        try {
            val firstDoseMillis = parseTimeToMillis(data.hour)
            NotificationUtils.scheduleNotification(
                context,
                "Recordatorio de medicamento",
                "Es hora de tomar ${data.name} (${data.dosage} ${data.unit})",
                firstDoseMillis,
                reminderId.hashCode()
            )

            if (data.frequency == "Diariamente" && data.secondHour.isNotBlank()) {
                val secondDoseMillis = parseTimeToMillis(data.secondHour)
                NotificationUtils.scheduleNotification(
                    context,
                    "Recordatorio de medicamento",
                    "Es hora de tomar ${data.name} (${data.dosage} ${data.unit})",
                    secondDoseMillis,
                    (reminderId.hashCode() + 1)
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
            data.dosage.isBlank() -> _errorMessage.value = "Por favor ingresa la dosis"
            data.unit.isBlank() -> _errorMessage.value = "Por favor selecciona la unidad"
            data.type.isBlank() -> _errorMessage.value = "Por favor selecciona el tipo de medicamento"
            data.frequency.isBlank() -> _errorMessage.value = "Por favor selecciona la frecuencia"
            data.hour.isBlank() -> _errorMessage.value = "Por favor selecciona la primera dosis"
            data.frequency == "Días Seleccionados" && data.days.isEmpty() ->
                _errorMessage.value = "Por favor selecciona al menos un día"
            data.frequency == "Cíclicamente" && data.cycleWeeks.isBlank() ->
                _errorMessage.value = "Por favor selecciona el ciclo de semanas"
            else -> return true
        }
        return false
    }
}