package com.rannunen.assistedreminder2023.data.entity

import androidx.room.*

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique=true),
        Index("reminder_category_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["reminder_category_id"],
            // What happens to the child when parent is changed
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id")val reminderId: Long = 0,
    @ColumnInfo(name = "reminder_title") val reminderTitle: String,
    @ColumnInfo(name = "reminder_date") val reminderDate: Long,
    @ColumnInfo(name = "reminder_category_id") val reminderCategoryId: Long,
    @ColumnInfo(name = "reminder_description") val reminderDescription: String,
    @ColumnInfo(name = "reminder_time") val reminderTime: Long,
    @ColumnInfo(name = "reminder_image") val reminderImage: String
    //add the information of the reminder, image?
)
