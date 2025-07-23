package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScheduleScreen(navController: NavController, viewModel: ReminderViewModel) {
    val purple = Color(0xFFB295C7)
    
    val formData by viewModel.formData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var frecuenciaExpanded by remember { mutableStateOf(false) }
    val frecuenciaOptions = listOf("Diariamente", "Días Seleccionados", "Cíclicamente")

    var periodoExpanded by remember { mutableStateOf(false) }
    val opcionesHora = listOf("6:00 a.m.", "7:00 a.m.", "8:00 a.m.", "9:00 a.m.", "12:00 p.m.", "6:00 p.m.")

    // Auto-ocultar mensajes de éxito después de unos segundos
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(3000) // 3 segundos
            viewModel.clearMessages()
        }
    }

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
                                formData.medication.ifBlank { "Medicamento" }, 
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
                        value = formData.frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { 
                            Text(
                                "¿Cada cuándo lo debes tomar?", 
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
                                    viewModel.updateFormData(formData.copy(frequency = option))
                                    frecuenciaExpanded = false
                                }
                            )
                        }
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
                    
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Hora de Dosis (editable)
                    OutlinedTextField(
                        value = formData.doseTime,
                        onValueChange = { 
                            viewModel.updateFormData(formData.copy(doseTime = it))
                        },
                            label = { 
                                Text(
                                    "Hora de dosis", 
                                    fontSize = 16.sp
                                ) 
                            },
                            placeholder = { 
                                Text(
                                    "Ej: 12:00 p.m.", 
                                    fontSize = 14.sp
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = purple,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Selección de 1ra Dosis
                    ExposedDropdownMenuBox(
                        expanded = periodoExpanded,
                        onExpandedChange = { periodoExpanded = !periodoExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = formData.firstDoseTime,
                            onValueChange = {},
                            readOnly = true,
                                label = { 
                                    Text(
                                        "1ra Dosis", 
                                        fontSize = 16.sp
                                    ) 
                                },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded)
                            },
                                modifier = Modifier.menuAnchor(),
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
                                        viewModel.updateFormData(formData.copy(firstDoseTime = hora))
                                        periodoExpanded = false
                                    }
                                )
                                }
                            }
                        }
                    }
                }

                // Mensajes de estado
                errorMessage?.let { error ->
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
                                text = "❌ $error",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                successMessage?.let { success ->
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
                                text = success,
                                color = Color.Green,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
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
                            if (viewModel.validateScheduleForm()) {
                            navController.navigate(Screen.ReminderProgram.route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purple),
                        enabled = !isLoading && formData.frequency.isNotBlank() && formData.firstDoseTime.isNotBlank(),
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