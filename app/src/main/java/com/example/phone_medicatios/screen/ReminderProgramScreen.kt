package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import com.example.phone_medicatios.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderProgramScreen(navController: NavController) {
    val purple = Color(0xFFB295C7)

    // Información simulada desde pasos anteriores
    val medicamento = "Paracetamol"
    val dosis = "500mg"
    val horarios = listOf("6:00a.m." to "1 píldora", "6:00p.m." to "1 píldora")
    val iconos = listOf(R.drawable.ic_sun, R.drawable.ic_moon) // Asegúrate de tener estos recursos

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

                // Pasos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem("1", "Elegir\nMedicamento", purple, false)
                    StepItem("2", "Configurar\nRecordatorio", purple, false)
                    StepItem("3", "Programa\nFinal", purple, true)
                }

                // Nombre y dosis del medicamento
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_medication),
                        contentDescription = null,
                        tint = purple,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(purple.copy(alpha = 0.2f))
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(medicamento, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(dosis, fontSize = 14.sp, color = Color.Gray)
                    }
                }

                // Horarios configurados
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, purple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    horarios.forEachIndexed { index, (hora, cantidad) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = iconos.getOrElse(index) { R.drawable.ic_clock }),
                                    contentDescription = null,
                                    tint = purple,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(hora, fontSize = 18.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pill),
                                    contentDescription = null,
                                    tint = purple,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(cantidad, fontSize = 18.sp, color = purple)
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
                        onClick = { /* Confirmar programación */ },
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
fun StepItem(number: String, label: String, color: Color, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (active) color else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = if (active) color else Color.Gray
        )
    }
}
