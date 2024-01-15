package com.rannunen.assistedreminder2023.data.database

import androidx.room.*
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao {
    // Get reminders of a specific category
    @Query("""SELECT reminders.* FROM reminders 
        INNER JOIN categories ON reminders.reminder_category_id = categories.id
        WHERE reminder_category_id = :categoryId
        """ )
    abstract fun remindersFromCategory(categoryId: Long): Flow<List<ReminderToCategory>>

    // Select one reminder with its id
    @Query("""SELECT * FROM reminders WHERE id = :reminderId""")
    abstract fun reminder(reminderId:Long): Reminder?

    // Insert reminder
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Suspect changes operating thread
    abstract fun insert(entity: Reminder): Long

    // Update reminder
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(entity: Reminder)

    // Delete reminder
    @Delete
    abstract fun delete(entity: Reminder): Int

}