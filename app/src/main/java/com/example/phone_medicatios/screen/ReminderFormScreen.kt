package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.phone_medicatios.R
import com.example.phone_medicatios.navigation.Screen
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderFormScreen(navController: NavController) {
    val purple = Color(0xFFB295C7)

    // Estados
    var medicamento by remember { mutableStateOf("Paracetamol") }
    var dosis by remember { mutableStateOf("500mg") }
    var unidad by remember { mutableStateOf("mg") }
    var tipo by remember { mutableStateOf("Tableta") }

    var unidadExpanded by remember { mutableStateOf(false) }
    val unidadOptions = listOf("mg", "g", "mls")

    var tipoExpanded by remember { mutableStateOf(false) }
    val tipoOptions = listOf("Tableta", "Líquido", "Cápsula")

    var horaDosis by remember { mutableStateOf("12:00 p.m.") }

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

                // Pasos del formulario centrado
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem("1", "Elegir\nMedicamento", purple, true)
                    StepItem("2", "Configurar\nRecordatorio", purple, false)
                    StepItem("3", "Programa\nFinal", purple, false)
                }

                // Campos
                ReminderDropdownField("Medicamento", medicamento) { medicamento = it }
                ReminderDropdownField("Dosis", dosis) { dosis = it }

                // Dropdown Unidad
                ExposedDropdownMenuBox(
                    expanded = unidadExpanded,
                    onExpandedChange = { unidadExpanded = !unidadExpanded }
                ) {
                    OutlinedTextField(
                        value = unidad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = unidadExpanded,
                        onDismissRequest = { unidadExpanded = false }
                    ) {
                        unidadOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    unidad = option
                                    unidadExpanded = false
                                }
                            )
                        }
                    }
                }

                // Dropdown Tipo
                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = !tipoExpanded }
                ) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false }
                    ) {
                        tipoOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    tipo = option
                                    tipoExpanded = false
                                }
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
                        onClick = { navController.popBackStack() },
                        border = BorderStroke(1.dp, purple),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = purple)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            navController.navigate(Screen.ReminderSchedule.route)
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


@Composable
fun ReminderDropdownField(label: String, value: String, onValueChange: (String) -> Unit) {
    val purple = Color(0xFFB295C7)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 16.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = purple,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = purple,
            cursorColor = purple
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
    )
}
