package com.rannunen.assistedreminder2023.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.rannunen.assistedreminder2023.ui.theme.AssistedReminder2023Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssistedReminder2023Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background)
                {
                    val notificationPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
                    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

                    LaunchedEffect(key1 = Unit){
                        notificationPermissionState.launchPermissionRequest()
                        cameraPermissionState.launchPermissionRequest()

                    }
                    if (notificationPermissionState.status.isGranted && cameraPermissionState.status.isGranted){
                        ReminderApp()
                    }else{
                        if(notificationPermissionState.status.shouldShowRationale ||
                            cameraPermissionState.status.shouldShowRationale){
                        }else {
                            Text("Permissions needed")
                        }
                    }

                }
            }
        }
    }
}
