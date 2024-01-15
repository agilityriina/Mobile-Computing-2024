package com.rannunen.assistedreminder2023.data.database

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import java.util.*

class ReminderToCategory {
    // Combine reminder with its category
    @Embedded
    lateinit var reminder: Reminder

    @Relation(parentColumn = "reminder_category_id", entityColumn = "id")
    lateinit var _categories: List<Category>

    @get:Ignore
    val category: Category
        get() = _categories[0]

    // Allow to be destructed

    operator fun component1() = reminder
    operator fun component2() = category

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ReminderToCategory -> reminder == other.reminder && _categories == other._categories
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(reminder, _categories)
}