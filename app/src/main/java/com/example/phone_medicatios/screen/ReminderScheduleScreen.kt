package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phone_medicatios.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phone_medicatios.viewmodel.ReminderViewModel
import com.example.phone_medicatios.navigation.Screen
import com.example.phone_medicatios.components.StepItem

// Función de cálculo automático eliminada - ahora es manual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScheduleScreen(navController: NavController, viewModel: ReminderViewModel) {
    val purple = Color(0xFFB295C7)
    
    val formData by viewModel.formData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Debug: Log del formData para verificar que los datos se preservan
    LaunchedEffect(formData) {
        android.util.Log.d("ReminderScheduleScreen", "formData actualizado: $formData")
    }
    
    // Campo de segunda dosis manual - no se calcula automáticamente

    var frecuenciaExpanded by remember { mutableStateOf(false) }
    val frecuenciaOptions = listOf("4", "6", "8", "12", "24")

    var periodoExpanded by remember { mutableStateOf(false) }
    var segundaDosisExpanded by remember { mutableStateOf(false) }
    
    // Variable para el campo de horas entre dosis
    var horasEntreDosis by remember { mutableStateOf("") }
    
    // Opciones completas de hora (24 horas)
    val opcionesHora = listOf(
        "12:00 a.m.", "1:00 a.m.", "2:00 a.m.", "3:00 a.m.", "4:00 a.m.", "5:00 a.m.",
        "6:00 a.m.", "7:00 a.m.", "8:00 a.m.", "9:00 a.m.", "10:00 a.m.", "11:00 a.m.",
        "12:00 p.m.", "1:00 p.m.", "2:00 p.m.", "3:00 p.m.", "4:00 p.m.", "5:00 p.m.",
        "6:00 p.m.", "7:00 p.m.", "8:00 p.m.", "9:00 p.m.", "10:00 p.m.", "11:00 p.m."
    )
    
    // Función para calcular la segunda dosis basada en la primera dosis y las horas
    fun calcularSegundaDosis(primeraDosis: String, horasEntreDosis: Int): String {
        if (primeraDosis.isBlank() || horasEntreDosis <= 0) return ""
        
        val horaMap = mapOf(
            "12:00 a.m." to 0, "1:00 a.m." to 1, "2:00 a.m." to 2, "3:00 a.m." to 3,
            "4:00 a.m." to 4, "5:00 a.m." to 5, "6:00 a.m." to 6, "7:00 a.m." to 7,
            "8:00 a.m." to 8, "9:00 a.m." to 9, "10:00 a.m." to 10, "11:00 a.m." to 11,
            "12:00 p.m." to 12, "1:00 p.m." to 13, "2:00 p.m." to 14, "3:00 p.m." to 15,
            "4:00 p.m." to 16, "5:00 p.m." to 17, "6:00 p.m." to 18, "7:00 p.m." to 19,
            "8:00 p.m." to 20, "9:00 p.m." to 21, "10:00 p.m." to 22, "11:00 p.m." to 23
        )
        
        val horaInicial = horaMap[primeraDosis] ?: return ""
        val horaFinal = (horaInicial + horasEntreDosis) % 24
        
        val horaAMPM = horaMap.entries.find { it.value == horaFinal }?.key ?: return ""
        return horaAMPM
    }
    
    // Campo de horas entre dosis ahora es manual
    
    var ciclosExpanded by remember { mutableStateOf(false) }
    val opcionesCiclos = listOf("2", "3", "4")
    
    val diasSemana = listOf("L", "M", "M", "J", "V", "S", "D")
    val nombresDias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Auto-ocultar mensajes de éxito después de unos segundos
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(3000) // 3 segundos
            viewModel.clearMessages()
        }
    }
    

    
    // No hay sincronización automática - el usuario ingresa manualmente

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Encabezado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_medication),
                        contentDescription = "Icono",
                        tint = purple,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(purple.copy(alpha = 0.1f))
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Configurar Horario", 
                            fontSize = 26.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Define cuándo tomar tu medicamento", 
                            fontSize = 16.sp, 
                            color = Color.Gray
                        )
                    }
                }

                // Pasos centrados
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem("1", "Elegir\nMedicamento", purple, false)
                    StepItem("2", "Configurar\nRecordatorio", purple, true)
                    StepItem("3", "Programa\nFinal", purple, false)
                }

                // Información del medicamento seleccionado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = purple.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_medication),
                            contentDescription = null,
                            tint = purple,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(purple.copy(alpha = 0.1f))
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                formData.name.ifBlank { "Medicamento" }, 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${formData.dosage}${formData.unit} - ${formData.type}", 
                                fontSize = 14.sp, 
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Campo de Frecuencia
                ExposedDropdownMenuBox(
                    expanded = frecuenciaExpanded,
                    onExpandedChange = { frecuenciaExpanded = !frecuenciaExpanded }
                ) {
                                            OutlinedTextField(
                            value = formData.frequencyHours.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { 
                                Text(
                                    "Frecuencia (cada X horas)", 
                                    fontSize = 16.sp
                                ) 
                            },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = frecuenciaExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = purple,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = frecuenciaExpanded,
                        onDismissRequest = { frecuenciaExpanded = false }
                    ) {
                        frecuenciaOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        option, 
                                        fontSize = 16.sp
                                    ) 
                                },
                                                                    onClick = {
                                        val frequencyHours = option.toIntOrNull() ?: 8
                                        viewModel.updateFormData(formData.copy(frequencyHours = frequencyHours))
                                        frecuenciaExpanded = false
                                    }
                            )
                        }
                    }
                }

                // Días seleccionados (siempre visible)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Selección de días", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.SemiBold,
                        color = purple
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        diasSemana.forEachIndexed { index, dia ->
                            val isSelected = formData.days.contains(nombresDias[index])
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) purple else Color.Gray.copy(alpha = 0.2f)
                                    )
                                    .clickable {
                                        val diaSeleccionado = nombresDias[index]
                                        val nuevosDias = if (isSelected) {
                                            formData.days - diaSeleccionado
                                        } else {
                                            formData.days + diaSeleccionado
                                        }
                                        viewModel.updateFormData(formData.copy(days = nuevosDias))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dia,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    if (formData.days.isNotEmpty()) {
                        Text(
                            "Días seleccionados: ${formData.days.joinToString(", ")}",
                            fontSize = 12.sp,
                            color = purple,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }



                // Horarios
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Horarios de toma", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.SemiBold,
                        color = purple
                    )
                    
                    // Primera dosis
                    ExposedDropdownMenuBox(
                        expanded = periodoExpanded,
                        onExpandedChange = { periodoExpanded = !periodoExpanded }
                    ) {
                        OutlinedTextField(
                            value = formData.firstHour,
                            onValueChange = {},
                            readOnly = true,
                            label = { 
                                Text(
                                    "Primera dosis", 
                                    fontSize = 16.sp
                                ) 
                            },
                            placeholder = { 
                                Text(
                                    "Selecciona la hora", 
                                    fontSize = 14.sp
                                ) 
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = purple,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = periodoExpanded,
                            onDismissRequest = { periodoExpanded = false }
                        ) {
                            opcionesHora.forEach { hora ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            hora, 
                                            fontSize = 16.sp
                                        ) 
                                    },
                                    onClick = {
                                        viewModel.updateFormData(formData.copy(firstHour = hora))
                                        periodoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    

                    

                    

                }

                // Mensajes de estado (solo si hay mensajes)
                if (errorMessage?.isNotBlank() == true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❌ $errorMessage",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.clearMessages() }
                            ) {
                                Text(
                                    "✕",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                if (successMessage?.isNotBlank() == true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = successMessage,
                                color = Color.Green,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.clearMessages() }
                            ) {
                                Text(
                                    "✕",
                                    color = Color.Green,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { 
                            viewModel.clearMessages()
                            navController.popBackStack() 
                        },
                        border = BorderStroke(2.dp, purple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = purple),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Atrás", 
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = {
                            viewModel.clearMessages()
                            try {
                                if (viewModel.validateScheduleForm()) {
                                    navController.navigate(Screen.ReminderProgram.route)
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("ReminderScheduleScreen", "Error al validar formulario: ${e.message}", e)
                                viewModel.setErrorMessage("Error al validar el formulario: ${e.message}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purple),
                        enabled = !isLoading && 
                                 formData.frequencyHours > 0 && 
                                 formData.firstHour.isNotBlank() && 
                                 formData.days.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Siguiente", 
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
} 