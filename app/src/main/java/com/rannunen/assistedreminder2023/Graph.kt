package com.rannunen.assistedreminder2023

import android.content.Context
import androidx.room.Room
import com.rannunen.assistedreminder2023.data.database.AssistedReminderDatabase
import com.rannunen.assistedreminder2023.data.repository.CategoryRepository
import com.rannunen.assistedreminder2023.data.repository.ReminderRepository

object Graph {
    lateinit var database: AssistedReminderDatabase
        private set


    val categoryRepository by lazy {
        CategoryRepository(
            categoryDao =  database.categoryDao()
        )
    }

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao =  database.reminderDao()
        )
    }


    fun provide(context: Context){
        database = Room.databaseBuilder(context, AssistedReminderDatabase::class.java, "data.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()       // Fix an error "Cannot access database on the main thread
            .build()
    }
}