package com.example.phone_medicatios.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun DashboardScreen(navController: NavHostController) {
    val purple = Color(0xFFB295C7)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 40.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_medication),
                    contentDescription = "User Icon",
                    tint = purple,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(purple.copy(alpha = 0.1f))
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Hola, Juana", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Algún mensaje inspirador. 🌤️", fontSize = 18.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Viernes, 11 de Julio del 2025.",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEachIndexed { i, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (i == 4) purple else Color.Gray.copy(alpha = 0.3f))
                        )
                        Text(day, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Registros del Día
            Text(
                "📝 Registros del Día",
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Tomadas",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            RegistroItem("Metilfenidato", "1 Píldora (10mg)", "7:00 a.m.", R.drawable.ic_pill_filled, purple)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Restantes",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            RegistroItem("Ibuprofeno", "1 Píldora (10mg)", "9:00 p.m.", R.drawable.ic_pill_half, purple)

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // Medicamentos registrados (solo texto)
            Text(
                "💊 Medicamentos Registrados",
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MedicamentoItem("Loratadina", "3 Píldoras (30mg)", R.drawable.ic_pill_filled, purple)
            MedicamentoItem("Ibuprofeno", "1/2 Píldora (5mg)", R.drawable.ic_pill_half, purple)
            MedicamentoItem("Paracetamol", "3 Píldoras (500mg)", R.drawable.ic_pill_filled, purple)

            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.ReminderForm.route) },
            containerColor = purple,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir",
                tint = Color.White
            )
        }
    }
}

@Composable
fun RegistroItem(nombre: String, dosis: String, hora: String, icon: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(nombre, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Text(dosis, fontSize = 18.sp, color = Color.Gray)
            }
        }
        Text(hora, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun MedicamentoItem(nombre: String, dosis: String, icon: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(nombre, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Text(dosis, fontSize = 16.sp, color = Color.Gray)
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_pill_half),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}
