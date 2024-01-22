package com.rannunen.assistedreminder2023.ui.Reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rannunen.assistedreminder2023.AssistedReminderApplication
import com.rannunen.assistedreminder2023.Graph
import com.rannunen.assistedreminder2023.R
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import com.rannunen.assistedreminder2023.data.repository.CategoryRepository
import com.rannunen.assistedreminder2023.data.repository.ReminderRepository
import com.rannunen.assistedreminder2023.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
): ViewModel(){
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long{
        // When new reminder is saved give notification
        newReminderNotification(reminder)
        return reminderRepository.addReminder(reminder)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
        viewModelScope.launch {
            // Collect gets a list of categories
            categoryRepository.categories().collect{ categories ->
                _state.value = ReminderViewState(categories)
            }
        }
    }


}

// Notifications
private fun createNotificationChannel(context: Context){
    // Check if support
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "NotificationChannel"
        val descriptionText = "Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }

        // Channel registration
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
val intent = Intent(Graph.appContext, MainActivity::class.java).apply {
   flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}
val pendingIntent: PendingIntent = PendingIntent.getActivity(Graph.appContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

private fun newReminderNotification(reminder: Reminder){
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("New Reminder!")
        .setContentText("${reminder.reminderTitle}" )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)


    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build()) // Something should be done abt this
    }
}


data class ReminderViewState(
    val categories: List<Category> = emptyList()
)
