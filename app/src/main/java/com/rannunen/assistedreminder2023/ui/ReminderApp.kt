package com.rannunen.assistedreminder2023.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rannunen.assistedreminder2023.ReminderAppState
import com.rannunen.assistedreminder2023.rememberReminderAppState
import com.rannunen.assistedreminder2023.ui.Reminder.Reminder
import com.rannunen.assistedreminder2023.ui.home.Home
import com.rannunen.assistedreminder2023.ui.login.Login
import com.rannunen.assistedreminder2023.ui.settings.Settings

@Composable
fun ReminderApp(
    appState: ReminderAppState = rememberReminderAppState()
){
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ){
        composable(route = "login") {
            Login(navController = appState.navController)
        }
        composable(route = "home"){
            Home(
                navController = appState.navController
            )
        }
        composable(route = "reminder"){
            Reminder(onBackPress = appState::navigateBack)
        }

        composable(route = "settings"){
            Settings(onBackPress = appState::navigateBack)
        }

    }
}