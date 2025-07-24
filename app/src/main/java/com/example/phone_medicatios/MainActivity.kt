package com.example.phone_medicatios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phone_medicatios.navigation.AppNavigation
import com.example.phone_medicatios.theme.MedicAppTheme
import com.example.phone_medicatios.viewmodel.ReminderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "Iniciando MainActivity...")
        
        try {
            setContent {
                MedicAppTheme {
                    Log.d("MainActivity", "Creando navegación...")
                    val navController = rememberNavController()
                    Log.d("MainActivity", "Creando ViewModel...")
                    val viewModel: ReminderViewModel = viewModel()
                    Log.d("MainActivity", "Inicializando AppNavigation...")
                    AppNavigation(navController, viewModel)
                    Log.d("MainActivity", "AppNavigation inicializado exitosamente")
                }
            }
            
            Log.d("MainActivity", "MainActivity iniciado exitosamente")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error crítico en MainActivity", e)
            // Mostrar un mensaje de error simple
            setContent {
                MedicAppTheme {
                    androidx.compose.material3.Text("Error al inicializar la aplicación: ${e.message}")
                }
            }
        }
    }
}