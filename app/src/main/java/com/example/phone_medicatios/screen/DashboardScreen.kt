package com.example.phone_medicatios.screen
import androidx.compose.runtime.collectAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.phone_medicatios.R
import com.example.phone_medicatios.navigation.Screen
import com.example.phone_medicatios.viewmodel.ReminderViewModel
import com.example.phone_medicatios.data.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(navController: NavHostController, viewModel: ReminderViewModel) {
    // Los datos se actualizan automáticamente en tiempo real, no necesitamos cargar manualmente
    
    val purple = Color(0xFFB295C7)

    val medications by viewModel.medications.collectAsState()
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Obtener fecha actual
    val currentDate = SimpleDateFormat("EEEE, dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        .format(Date())

    // Auto-ocultar mensajes de éxito después de unos segundos
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            kotlinx.coroutines.delay(3000) // 3 segundos
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = purple,
                    modifier = Modifier.size(48.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(vertical = 40.dp)
            ) {
                // Header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_medication),
                            contentDescription = "User Icon",
                            tint = purple,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(purple.copy(alpha = 0.1f))
                                .padding(12.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Bienvenido", 
                                fontSize = 28.sp, 
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "¡Mantén tu salud al día! 💊", 
                                fontSize = 16.sp, 
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                        
                        // Botón de historial
                        IconButton(
                            onClick = { navController.navigate(Screen.History.route) },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(purple.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = "Historial",
                                tint = purple,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Fecha actual
                    Text(
                        currentDate,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }

                // Estadísticas
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tarjeta de medicamentos
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = purple.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { navController.navigate(Screen.Medications.route) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_medication),
                                    contentDescription = null,
                                    tint = purple,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "${stats.totalMedications}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = purple,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Medicamentos",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Ver todos",
                                    fontSize = 12.sp,
                                    color = purple,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // Tarjeta de recordatorios
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp),
                            onClick = { navController.navigate(Screen.History.route) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    contentDescription = null,
                                    tint = Color.Green,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "${stats.activeReminders}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Recordatorios",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Ver todos",
                                    fontSize = 12.sp,
                                    color = Color.Green,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Tarjeta de progreso del día
                item {
                    Card(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = Color.Blue,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Progreso del día",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${stats.completedToday} de ${stats.completedToday + stats.pendingToday} completadas",
                                    fontSize = 15.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "${if (stats.completedToday + stats.pendingToday > 0) (stats.completedToday * 100 / (stats.completedToday + stats.pendingToday)) else 0}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Botones de acción
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Botón de crear recordatorio
                        Button(
                            onClick = { navController.navigate(Screen.ReminderForm.route) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = purple),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Nuevo Recordatorio", 
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Botón de medicamentos
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Medications.route) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = purple
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = com.example.phone_medicatios.R.drawable.ic_medication),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = purple
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Medicamentos", 
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Mensajes de estado
                errorMessage?.let { error ->
                    if (error.isNotBlank()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        error,
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = { viewModel.clearMessages() }
                                    ) {
                                        Text(
                                            "✕",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                successMessage?.let { success ->
                    if (success.isNotBlank()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        success,
                                        color = Color.Green,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = { viewModel.clearMessages() }
                                    ) {
                                        Text(
                                            "✕",
                                            color = Color.Green,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Spacer antes de pendientes de hoy
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Horarios del día
                item {
                    Text(
                        "Pendientes de hoy",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                    )
                }

                // Filtrar solo los horarios no completados
                val pendingSchedules = todaySchedules.filter { !it.isCompleted }
                
                if (pendingSchedules.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = purple.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = null,
                                    tint = purple,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    "¡Excelente trabajo!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Ya tomaste todos tus medicamentos de hoy",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(pendingSchedules) { schedule ->
                        TodayScheduleCard(
                            schedule = schedule,
                            onMarkCompleted = { 
                                viewModel.markScheduleAsCompleted(schedule.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayScheduleCard(
    schedule: TodaySchedule,
    onMarkCompleted: () -> Unit
) {
    val purple = Color(0xFFB295C7)
    val green = Color(0xFF4CAF50)
    val orange = Color(0xFFFF9800)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        border = if (schedule.isOverdue) BorderStroke(2.dp, orange.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del medicamento
            Icon(
                painter = painterResource(id = R.drawable.ic_medication),
                contentDescription = null,
                tint = if (schedule.isOverdue) orange else purple,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (schedule.isOverdue) orange.copy(alpha = 0.1f) else purple.copy(alpha = 0.1f)
                    )
                    .padding(14.dp)
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // Información del horario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        schedule.medicationName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    if (schedule.isOverdue) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = orange.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Atrasado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = orange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    "${schedule.dosage} - ${schedule.time}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            
            // Botón de completar
            IconButton(
                onClick = onMarkCompleted,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(green.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Marcar como completado",
                    tint = green,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
