package com.example.phone_medicatios.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.phone_medicatios.screen.DashboardScreen
import com.example.phone_medicatios.screen.ReminderFormScreen
import com.example.phone_medicatios.screen.ReminderScheduleScreen
import com.example.phone_medicatios.screen.ReminderProgramScreen
import com.example.phone_medicatios.screen.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object ReminderForm : Screen("reminder_form")
    object ReminderSchedule : Screen("reminder_schedule")
    object ReminderProgram : Screen("reminder_program")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.ReminderForm.route) { ReminderFormScreen(navController) }
        composable(Screen.ReminderSchedule.route) { ReminderScheduleScreen(navController) }
        composable(Screen.ReminderProgram.route) { ReminderProgramScreen(navController) }
    }
}
