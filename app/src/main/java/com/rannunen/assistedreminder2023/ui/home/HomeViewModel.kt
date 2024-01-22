package com.rannunen.assistedreminder2023.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.rannunen.assistedreminder2023.Graph
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.rannunen.assistedreminder2023.R
import com.rannunen.assistedreminder2023.ui.MainActivity
import com.rannunen.assistedreminder2023.ui.api.SpotPriceManager
import com.rannunen.assistedreminder2023.util.SpotPriceWorker

class HomeViewModel (
    private val categoryRepository : CategoryRepository = Graph.categoryRepository,
) : ViewModel(){
    private val _state = MutableStateFlow(HomeViewState())
    private val _selectedCategory = MutableStateFlow<Category?>(null)



    // Make states available
    val state: StateFlow<HomeViewState>
        get() = _state

    // Select category from the top category row
    fun onCategorySelected(category: Category){
        _selectedCategory.value = category
    }

    init{
        createNotificationChannel(context = Graph.appContext)
        //setOneTimeNotification()
        setSpotNotification()
        //Multithreading with coroutine
        viewModelScope.launch{
            combine(
                categoryRepository.categories().onEach { list ->
                    //If no category is selected and list exists -> Pick first category
                    if (list.isNotEmpty() && _selectedCategory.value == null){
                        _selectedCategory.value = list[0]
                    }
                },
                _selectedCategory
            ){  categories, selectedCategory ->
                HomeViewState(
                    categories = categories,
                    selectedCategory = selectedCategory
                )

            }. collect { _state.value = it }
        }
        loadCategoriesFromDb()
    }
    private fun loadCategoriesFromDb() {
        val list = mutableListOf(
            Category(name = "Very Low"),
            Category(name = "Low"),
            Category(name = "Normal"),
            Category(name = "High"),
            Category(name = "Very High"),
        )
        viewModelScope.launch {
            list.forEach { category -> categoryRepository.addCategory(category) }
        }
    }
}

// Notifications


private fun setSpotNotification() {
    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Needs internet
        .build()

    val spotWorker = PeriodicWorkRequestBuilder<SpotPriceWorker>(30, TimeUnit.SECONDS)
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(spotWorker)

    // Monitor the state
    workManager.getWorkInfoByIdLiveData(spotWorker.id)
        .observeForever { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED || workInfo.state == WorkInfo.State.RUNNING || workInfo.state == WorkInfo.State.ENQUEUED){
                // Periodic won't return SUCCEED ever ....
                createSuccessNotification()
            } else{
                Log.i("SPOT", "should do failure")
                createFailureNotification()
            }
        }
}
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


private fun createSuccessNotification(){
    val notificationId = 1
    val priceNow = getSpotPrice().toString()
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Spot Price this hour is:")
        .setContentText("$priceNow")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build()) // Something should be done abt this
    }
}

private fun createFailureNotification(){
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Failure!")
        .setContentText("Countdown failed!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build()) // Something should be done abt this
    }
}

// This gets the updated spotprice for notification
fun getSpotPrice(): Double {
    // Access the spot price from SpotPriceManager
    return SpotPriceManager.spotPrice
}

data class HomeViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)
