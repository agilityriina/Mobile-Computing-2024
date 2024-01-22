package com.rannunen.assistedreminder2023.ui.api

import android.util.Log
import com.rannunen.assistedreminder2023.data.api.SpotPriceResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.util.*

interface SpotPriceService {
    @GET("price.json")
    suspend fun getSpotPrice(
        @Query("date") date: String,
        @Query("hour") hour: Int
    ):SpotPriceResponse
}

object SpotApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.porssisahko.net/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val spotPriceService:SpotPriceService= retrofit.create(SpotPriceService::class.java)
}

// Spot price
object SpotPriceManager {
    @Volatile
    var spotPrice: Double = 0.0
        private set

    // Update spot price
    fun updateSpotPrice(newSpotPrice: Double) {
        spotPrice = newSpotPrice
    }
}

