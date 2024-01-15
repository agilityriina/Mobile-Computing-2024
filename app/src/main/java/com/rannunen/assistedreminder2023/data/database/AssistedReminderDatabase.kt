package com.rannunen.assistedreminder2023.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder

@Database(
    entities = [Category::class, Reminder::class],
    version = 1,
    exportSchema = false

)
abstract class AssistedReminderDatabase :RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
}