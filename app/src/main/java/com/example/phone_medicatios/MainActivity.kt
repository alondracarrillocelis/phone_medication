package com.example.phone_medicatios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.phone_medicatios.navigation.AppNavigation
import com.example.phone_medicatios.theme.MedicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}