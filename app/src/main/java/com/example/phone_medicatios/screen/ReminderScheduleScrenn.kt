package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScheduleScreen(navController: NavController) {
    val purple = Color(0xFFB295C7)

    var frecuencia by remember { mutableStateOf("Diariamente") }
    var frecuenciaExpanded by remember { mutableStateOf(false) }
    val frecuenciaOptions = listOf("Diariamente", "Días Seleccionados", "Cíclicamente")

    var horaDosis by remember { mutableStateOf("12:00 p.m.") }
    var primeraDosis by remember { mutableStateOf("6:00 a.m.") }
    var periodoExpanded by remember { mutableStateOf(false) }
    val opcionesHora = listOf("6:00 a.m.", "7:00 a.m.", "8:00 a.m.", "9:00 a.m.", "12:00 p.m.", "6:00 p.m.")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Encabezado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_medication),
                        contentDescription = "Icono",
                        tint = purple,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(purple.copy(alpha = 0.1f))
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Nuevo Recordatorio", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Texto explicativo o algo...", fontSize = 17.sp, color = Color.Gray)
                    }
                }

                // Pasos centrados
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem("1", "Elegir\nMedicamento", purple, false)
                    StepItem("2", "Configurar\nRecordatorio", purple, true)
                    StepItem("3", "Programa\nFinal", purple, false)
                }

                // Campo de Frecuencia
                ExposedDropdownMenuBox(
                    expanded = frecuenciaExpanded,
                    onExpandedChange = { frecuenciaExpanded = !frecuenciaExpanded }
                ) {
                    OutlinedTextField(
                        value = frecuencia,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("¿Cada cuándo lo debes tomar?", fontSize = 16.sp) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = frecuenciaExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = frecuenciaExpanded,
                        onDismissRequest = { frecuenciaExpanded = false }
                    ) {
                        frecuenciaOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    frecuencia = option
                                    frecuenciaExpanded = false
                                }
                            )
                        }
                    }
                }

                // Horario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Hora de Dosis (editable)
                    OutlinedTextField(
                        value = horaDosis,
                        onValueChange = { horaDosis = it },
                        label = { Text("Hora de dosis", fontSize = 16.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    // Selección de 1ra Dosis
                    ExposedDropdownMenuBox(
                        expanded = periodoExpanded,
                        onExpandedChange = { periodoExpanded = !periodoExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = primeraDosis,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("1ra Dosis", fontSize = 16.sp) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = periodoExpanded,
                            onDismissRequest = { periodoExpanded = false }
                        ) {
                            opcionesHora.forEach { hora ->
                                DropdownMenuItem(
                                    text = { Text(hora) },
                                    onClick = {
                                        primeraDosis = hora
                                        periodoExpanded = false
                                    }
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
                        onClick = { navController.popBackStack() },
                        border = BorderStroke(1.dp, purple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = purple)
                    ) {
                        Text("Atras")
                    }
                    Button(
                        onClick = {
                            navController.navigate(com.example.phone_medicatios.navigation.Screen.ReminderProgram.route)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purple)
                    ) {
                        Text("Siguiente")
                    }

                }
            }
        }
    }
}
