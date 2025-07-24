package com.example.phone_medicatios.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusMessages(
    errorMessage: String?,
    successMessage: String?,
    onDismiss: () -> Unit
) {
    var showError by remember { mutableStateOf(errorMessage != null) }
    var showSuccess by remember { mutableStateOf(successMessage != null) }
    
    LaunchedEffect(errorMessage) {
        showError = errorMessage != null
    }
    
    LaunchedEffect(successMessage) {
        showSuccess = successMessage != null
    }
    
    if (showError && errorMessage != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = {
                        showError = false
                        onDismiss()
                    }
                ) {
                    Text("✕", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    if (showSuccess && successMessage != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = successMessage,
                    color = Color.Green,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = {
                        showSuccess = false
                        onDismiss()
                    }
                ) {
                    Text("✕", color = Color.Green, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
} 