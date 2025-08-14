package com.example.phone_medicatios.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HybridRepository(
    private val context: Context,
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(),
    private val sqliteRepository: SqliteRepository = SqliteRepository(context)
) : Repository {
    
    companion object {
        private const val TAG = "HybridRepository"
    }
    
    // StateFlows para datos locales que se actualizan inmediatamente
    private val _localMedications = MutableStateFlow<List<Medication>>(emptyList())
    private val _localReminders = MutableStateFlow<List<Reminder>>(emptyList())
    private val _localTodaySchedules = MutableStateFlow<List<TodaySchedule>>(emptyList())
    
    // Inicializar datos locales
    init {
        // Inicializar con listas vacías, los datos se cargarán cuando se necesiten
        Log.d(TAG, "HybridRepository inicializado")
        
        // Cargar datos locales en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            loadLocalData()
        }
    }
    
    // Método para cargar datos locales de forma asíncrona
    suspend fun loadLocalData() {
        try {
            val medications = sqliteRepository.getMedications()
            val reminders = sqliteRepository.getReminders()
            val todaySchedules = sqliteRepository.getTodaySchedules()
            
            _localMedications.value = medications
            _localReminders.value = reminders
            _localTodaySchedules.value = todaySchedules
            
            Log.d(TAG, "Datos locales cargados: ${medications.size} medicamentos, ${reminders.size} recordatorios")
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando datos locales: ${e.message}")
        }
    }
    
    // Métodos para obtener datos (prioridad: local primero, luego Firebase)
    override suspend fun getMedications(): List<Medication> {
        return try {
            // Intentar obtener de Firebase primero
            val firebaseMedications = firebaseRepository.getMedications()
            
            // Sincronizar con local
            syncMedicationsToLocal(firebaseMedications)
            
            // Retornar datos locales actualizados
            _localMedications.value
        } catch (e: Exception) {
            Log.w(TAG, "Error obteniendo medicamentos de Firebase, usando locales: ${e.message}")
            _localMedications.value
        }
    }
    
    override suspend fun getReminders(): List<Reminder> {
        return try {
            // Intentar obtener de Firebase primero
            val firebaseReminders = firebaseRepository.getReminders()
            
            // Sincronizar con local
            syncRemindersToLocal(firebaseReminders)
            
            // Retornar datos locales actualizados
            _localReminders.value
        } catch (e: Exception) {
            Log.w(TAG, "Error obteniendo recordatorios de Firebase, usando locales: ${e.message}")
            _localReminders.value
        }
    }
    
    override suspend fun getTodaySchedules(): List<TodaySchedule> {
        return try {
            // Intentar obtener de Firebase primero
            val firebaseSchedules = firebaseRepository.getTodaySchedules()
            
            // Sincronizar con local
            syncTodaySchedulesToLocal(firebaseSchedules)
            
            // Retornar datos locales actualizados
            _localTodaySchedules.value
        } catch (e: Exception) {
            Log.w(TAG, "Error obteniendo horarios de Firebase, usando locales: ${e.message}")
            _localTodaySchedules.value
        }
    }
    
    // Flows en tiempo real (combinan datos locales y Firebase)
    override fun getMedicationsFlow(): Flow<List<Medication>> {
        return _localMedications.asStateFlow()
    }
    
    override fun getRemindersFlow(): Flow<List<Reminder>> {
        return _localReminders.asStateFlow()
    }
    
    // Flow para horarios de hoy en tiempo real
    fun getTodaySchedulesFlow(): Flow<List<TodaySchedule>> {
        return _localTodaySchedules.asStateFlow()
    }
    
    // Métodos para agregar datos (guardar en ambos)
    override suspend fun addMedication(medication: Medication): String? {
        return try {
            // Guardar en local primero (para respuesta inmediata)
            val localId = sqliteRepository.addMedication(medication)
            
            // Actualizar estado local inmediatamente
            val updatedMedications = _localMedications.value + medication.copy(id = localId ?: "")
            _localMedications.value = updatedMedications
            
            Log.d(TAG, "Medicamento agregado localmente con ID: $localId")
            
            // Intentar guardar en Firebase (en segundo plano)
            try {
                val firebaseId = firebaseRepository.addMedication(medication)
                if (firebaseId != null) {
                    // Actualizar ID local con el de Firebase
                    sqliteRepository.updateMedicationId(localId ?: "", firebaseId)
                    Log.d(TAG, "Medicamento sincronizado con Firebase: $firebaseId")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
            }
            
            localId
        } catch (e: Exception) {
            Log.e(TAG, "Error agregando medicamento: ${e.message}")
            null
        }
    }
    
    override suspend fun addReminder(reminder: Reminder): String? {
        return try {
            // Crear el medicamento asociado al recordatorio
            val medication = Medication(
                name = reminder.name,
                dosage = reminder.dosage.toString(),
                unit = reminder.unit,
                type = reminder.type,
                description = reminder.instructions, // Usar instructions como description
                instructions = reminder.instructions,
                userId = reminder.userId,
                createdAt = reminder.createdAt,
                isActive = true
            )
            
            // Guardar medicamento primero
            val medicationId = addMedication(medication)
            
            Log.d(TAG, "Recordatorio con frecuencia de ${reminder.frequencyHours} horas")
            
            // Guardar en local primero (para respuesta inmediata)
            val localId = sqliteRepository.addReminder(reminder)
            
            // Actualizar estado local inmediatamente
            val updatedReminders = _localReminders.value + reminder.copy(id = localId ?: "")
            _localReminders.value = updatedReminders
            
            // Actualizar horarios de hoy con todas las dosis
            updateLocalTodaySchedules()
            
            Log.d(TAG, "Recordatorio agregado localmente con ID: $localId")
            
            // Intentar guardar en Firebase (en segundo plano)
            var finalId = localId
            try {
                val firebaseId = firebaseRepository.addReminder(reminder)
                if (firebaseId != null) {
                    // Actualizar ID local con el de Firebase
                    sqliteRepository.updateReminderId(localId ?: "", firebaseId)
                    finalId = firebaseId
                    Log.d(TAG, "Recordatorio sincronizado con Firebase: $firebaseId")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
                // Si Firebase falla, pero local funcionó, seguimos con el ID local
            }
            
            // Siempre devolver un ID válido si se guardó localmente
            finalId ?: localId ?: "temp_${System.currentTimeMillis()}"
        } catch (e: Exception) {
            Log.e(TAG, "Error agregando recordatorio: ${e.message}")
            null
        }
    }
    
    // Métodos para actualizar datos
    override suspend fun updateMedication(medication: Medication): Boolean {
        return try {
            // Actualizar en local primero
            val localSuccess = sqliteRepository.updateMedication(medication)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedMedications = _localMedications.value.map { 
                    if (it.id == medication.id) medication else it 
                }
                _localMedications.value = updatedMedications
                
                Log.d(TAG, "Medicamento actualizado localmente")
            }
            
            // Intentar actualizar en Firebase (en segundo plano)
            try {
                firebaseRepository.updateMedication(medication)
                Log.d(TAG, "Medicamento sincronizado con Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando medicamento: ${e.message}")
            false
        }
    }
    
    override suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            // No necesitamos calcular dosis adicionales, se calculan automáticamente
            val reminderToUpdate = reminder
            
            Log.d(TAG, "Actualizando recordatorio con frecuencia de ${reminderToUpdate.frequencyHours} horas")
            
            // Actualizar en local primero
            val localSuccess = sqliteRepository.updateReminder(reminderToUpdate)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedReminders = _localReminders.value.map { 
                    if (it.id == reminderToUpdate.id) reminderToUpdate else it 
                }
                _localReminders.value = updatedReminders
                
                // Actualizar horarios de hoy
                updateLocalTodaySchedules()
                
                Log.d(TAG, "Recordatorio actualizado localmente")
            }
            
            // Intentar actualizar en Firebase (en segundo plano)
            try {
                firebaseRepository.updateReminder(reminderToUpdate)
                Log.d(TAG, "Recordatorio sincronizado con Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando recordatorio: ${e.message}")
            false
        }
    }
    
    // Métodos para eliminar datos
    override suspend fun deleteMedication(medicationId: String): Boolean {
        return try {
            // Eliminar de local primero
            val localSuccess = sqliteRepository.deleteMedication(medicationId)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedMedications = _localMedications.value.filter { it.id != medicationId }
                _localMedications.value = updatedMedications
                
                Log.d(TAG, "Medicamento eliminado localmente")
            }
            
            // Intentar eliminar de Firebase (en segundo plano)
            try {
                firebaseRepository.deleteMedication(medicationId)
                Log.d(TAG, "Medicamento eliminado de Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error eliminando de Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando medicamento: ${e.message}")
            false
        }
    }
    
    override suspend fun deleteReminder(reminderId: String): Boolean {
        return try {
            // Eliminar de local primero
            val localSuccess = sqliteRepository.deleteReminder(reminderId)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedReminders = _localReminders.value.filter { it.id != reminderId }
                _localReminders.value = updatedReminders
                
                // Actualizar horarios de hoy
                updateLocalTodaySchedules()
                
                Log.d(TAG, "Recordatorio eliminado localmente")
            }
            
            // Intentar eliminar de Firebase (en segundo plano)
            try {
                firebaseRepository.deleteReminder(reminderId)
                Log.d(TAG, "Recordatorio eliminado de Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error eliminando de Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando recordatorio: ${e.message}")
            false
        }
    }
    
    // Métodos para marcar como completado
    override suspend fun markReminderCompleted(reminderId: String, completed: Boolean): Boolean {
        return try {
            // Actualizar en local primero
            val localSuccess = sqliteRepository.markReminderCompleted(reminderId, completed)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedReminders = _localReminders.value.map { reminder ->
                    if (reminder.id == reminderId) {
                        reminder.copy(completed = completed, active = !completed)
                    } else {
                        reminder
                    }
                }
                _localReminders.value = updatedReminders
                
                // Actualizar horarios de hoy
                updateLocalTodaySchedules()
                
                Log.d(TAG, "Recordatorio marcado como completado localmente")
            }
            
            // Intentar actualizar en Firebase (en segundo plano)
            try {
                firebaseRepository.markReminderCompleted(reminderId, completed)
                Log.d(TAG, "Recordatorio marcado como completado en Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error marcando recordatorio como completado: ${e.message}")
            false
        }
    }
    
    override suspend fun markDoseAsCompleted(reminderId: String, doseIndex: Int): Boolean {
        return try {
            // Buscar el recordatorio
            val reminder = _localReminders.value.find { it.id == reminderId }
            if (reminder == null) {
                Log.e(TAG, "Recordatorio no encontrado: $reminderId")
                return false
            }
            
            // Actualizar dosis completadas (toggle)
            val updatedCompletedDoses = if (reminder.isDoseCompleted(doseIndex)) {
                // Si ya está completada, la removemos
                reminder.completedDoses.filter { it != doseIndex }
            } else {
                // Si no está completada, la agregamos
                reminder.completedDoses + doseIndex
            }
            
            val updatedReminder = reminder.copy(completedDoses = updatedCompletedDoses)
            
            // Actualizar en local primero
            val localSuccess = sqliteRepository.updateReminder(updatedReminder)
            
            if (localSuccess) {
                // Actualizar estado local inmediatamente
                val updatedReminders = _localReminders.value.map { 
                    if (it.id == reminderId) updatedReminder else it 
                }
                _localReminders.value = updatedReminders
                
                // Actualizar horarios de hoy
                updateLocalTodaySchedules()
                
                Log.d(TAG, "Dosis $doseIndex marcada como completada localmente")
            }
            
            // Intentar actualizar en Firebase (en segundo plano)
            try {
                firebaseRepository.updateReminder(updatedReminder)
                Log.d(TAG, "Dosis marcada como completada en Firebase")
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Firebase: ${e.message}")
            }
            
            localSuccess
        } catch (e: Exception) {
            Log.e(TAG, "Error marcando dosis como completada: ${e.message}")
            false
        }
    }
    
    // Métodos de sincronización
    private suspend fun syncMedicationsToLocal(firebaseMedications: List<Medication>) {
        try {
            // Sincronizar medicamentos de Firebase con local
            firebaseMedications.forEach { medication ->
                val existingLocal = sqliteRepository.getMedicationById(medication.id)
                if (existingLocal == null) {
                    sqliteRepository.addMedication(medication)
                } else if (existingLocal != medication) {
                    sqliteRepository.updateMedication(medication)
                }
            }
            
            // Actualizar estado local
            val localMedications = sqliteRepository.getMedications()
            _localMedications.value = localMedications
            
            Log.d(TAG, "Medicamentos sincronizados: ${localMedications.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sincronizando medicamentos: ${e.message}")
        }
    }
    
    private suspend fun syncRemindersToLocal(firebaseReminders: List<Reminder>) {
        try {
            // Sincronizar recordatorios de Firebase con local
            firebaseReminders.forEach { reminder ->
                val existingLocal = sqliteRepository.getReminderById(reminder.id)
                if (existingLocal == null) {
                    sqliteRepository.addReminder(reminder)
                } else if (existingLocal != reminder) {
                    sqliteRepository.updateReminder(reminder)
                }
            }
            
            // Actualizar estado local
            val localReminders = sqliteRepository.getReminders()
            _localReminders.value = localReminders
            
            Log.d(TAG, "Recordatorios sincronizados: ${localReminders.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sincronizando recordatorios: ${e.message}")
        }
    }
    
    private suspend fun syncTodaySchedulesToLocal(firebaseSchedules: List<TodaySchedule>) {
        try {
            // Sincronizar horarios de Firebase con local
            firebaseSchedules.forEach { schedule ->
                sqliteRepository.addOrUpdateTodaySchedule(schedule)
            }
            
            // Actualizar estado local
            val localSchedules = sqliteRepository.getTodaySchedules()
            _localTodaySchedules.value = localSchedules
            
            Log.d(TAG, "Horarios sincronizados: ${localSchedules.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sincronizando horarios: ${e.message}")
        }
    }
    
    // Método para actualizar horarios locales de hoy
    private suspend fun updateLocalTodaySchedules() {
        try {
            val todaySchedules = mutableListOf<TodaySchedule>()
            
            _localReminders.value.forEach { reminder ->
                if (reminder.active) {
                    // Calcular horarios basados en la frecuencia
                    val calculatedHours = reminder.calculateSchedule()
                    
                    Log.d(TAG, "Generando horarios para ${reminder.name}: ${calculatedHours.size} dosis - $calculatedHours")
                    
                    calculatedHours.forEachIndexed { index, hour ->
                        val schedule = TodaySchedule(
                            id = "${reminder.id}_$index",
                            reminderId = reminder.id,
                            medicationName = reminder.name,
                            dosage = "${reminder.dosage} ${reminder.unit}",
                            time = hour,
                            isCompleted = reminder.isDoseCompleted(index),
                            isOverdue = false // TODO: Implementar lógica de atraso
                        )
                        todaySchedules.add(schedule)
                    }
                }
            }
            
            _localTodaySchedules.value = todaySchedules
            Log.d(TAG, "Horarios de hoy actualizados: ${todaySchedules.size} dosis")
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando horarios locales: ${e.message}")
        }
    }
    

    
    // Método para resetear dosis completadas al cambiar de día
    suspend fun resetDailyDoses() {
        try {
            Log.d(TAG, "Reseteando dosis completadas para el nuevo día...")
            
            val updatedReminders = _localReminders.value.map { reminder ->
                // Resetear todos los recordatorios activos
                reminder.copy(completedDoses = emptyList())
            }
            
            // Actualizar en local
            updatedReminders.forEach { reminder ->
                sqliteRepository.updateReminder(reminder)
            }
            
            // Actualizar estado local
            _localReminders.value = updatedReminders
            
            // Actualizar horarios de hoy
            updateLocalTodaySchedules()
            
            // Sincronizar con Firebase en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    updatedReminders.forEach { reminder ->
                        firebaseRepository.updateReminder(reminder)
                    }
                    Log.d(TAG, "Dosis reseteadas en Firebase")
                } catch (e: Exception) {
                    Log.w(TAG, "Error sincronizando reseteo con Firebase: ${e.message}")
                }
            }
            
            Log.d(TAG, "Dosis reseteadas para el nuevo día")
        } catch (e: Exception) {
            Log.e(TAG, "Error reseteando dosis: ${e.message}")
        }
    }
    
    // Método para forzar sincronización completa
    suspend fun forceSync() {
        try {
            Log.d(TAG, "Iniciando sincronización forzada...")
            
            // Obtener datos de Firebase
            val firebaseMedications = firebaseRepository.getMedications()
            val firebaseReminders = firebaseRepository.getReminders()
            val firebaseSchedules = firebaseRepository.getTodaySchedules()
            
            // Sincronizar con local
            syncMedicationsToLocal(firebaseMedications)
            syncRemindersToLocal(firebaseReminders)
            syncTodaySchedulesToLocal(firebaseSchedules)
            
            Log.d(TAG, "Sincronización forzada completada")
        } catch (e: Exception) {
            Log.e(TAG, "Error en sincronización forzada: ${e.message}")
        }
    }
    

    
    // Implementación del método getUserStats requerido por la interfaz Repository
    override suspend fun getUserStats(): MedicationStats {
        return try {
            // Intentar obtener estadísticas de Firebase primero
            val firebaseStats = firebaseRepository.getUserStats()
            
            // Por ahora, retornar las estadísticas de Firebase
            // TODO: Implementar lógica híbrida más sofisticada
            firebaseStats
        } catch (e: Exception) {
            Log.w(TAG, "Error obteniendo estadísticas de Firebase, usando locales: ${e.message}")
            
            // Fallback a estadísticas locales
            val localMedications = _localMedications.value
            val localReminders = _localReminders.value
            
            val activeReminders = localReminders.count { it.active }
            var totalCompletedDoses = 0
            var totalPendingDoses = 0
            
            localReminders.forEach { reminder ->
                if (reminder.active) {
                    val completedDoses = reminder.getCompletedDosesCount()
                    val totalDoses = reminder.getTotalDosesCount()
                    totalCompletedDoses += completedDoses
                    totalPendingDoses += (totalDoses - completedDoses)
                }
            }
            
            MedicationStats(
                totalMedications = localMedications.size,
                activeReminders = activeReminders,
                completedToday = totalCompletedDoses,
                pendingToday = totalPendingDoses
            )
        }
    }
}
