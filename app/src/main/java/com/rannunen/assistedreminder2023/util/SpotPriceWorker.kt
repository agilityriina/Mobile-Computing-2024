package com.rannunen.assistedreminder2023.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rannunen.assistedreminder2023.ui.api.SpotApi
import com.rannunen.assistedreminder2023.ui.api.SpotPriceManager
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class SpotPriceWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters){

    override fun doWork(): Result {
        return try {
            val response = runBlocking {
                // Get the spotprice
                withContext(Dispatchers.IO) {
                    // The date and time are needed for the API
                    val dateNow = getDateString()
                    val calendar =  Calendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"))
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    SpotApi.spotPriceService.getSpotPrice(dateNow,currentHour)
                }
            }
            // Update new price to the variable because PeriodicWorkRequest cant move data...
            SpotPriceManager.updateSpotPrice(response.price)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

// Get date string for query
private fun getDateString(): String {
    val dateTime = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(dateTime)
}
