package com.rannunen.assistedreminder2023.data.repository

import com.rannunen.assistedreminder2023.data.database.ReminderDao
import com.rannunen.assistedreminder2023.data.database.ReminderToCategory
import com.rannunen.assistedreminder2023.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

// Reminder data repository
class ReminderRepository (
    private val reminderDao: ReminderDao
){
    fun remindersInCategory(categoryId: Long) : Flow<List<ReminderToCategory>> {
        return reminderDao.remindersFromCategory(categoryId)
    }

    // Add new reminder
    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)
}