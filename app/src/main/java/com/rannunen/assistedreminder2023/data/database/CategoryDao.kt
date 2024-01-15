package com.rannunen.assistedreminder2023.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rannunen.assistedreminder2023.data.entity.Category
import kotlinx.coroutines.flow.Flow

// Queries

@Dao
abstract class CategoryDao {

    // Get data
    @Query(value = "SELECT * FROM categories WHERE name = :name")
    abstract  fun getCategoryWithName(name: String): Category?

    // Select list
    @Query(value = "SELECT * FROM categories LIMIT 10")
    abstract fun categories(): Flow<List<Category>>

    // Select with id
    @Query(value = "SELECT * FROM categories WHERE id = :categoryId")
    abstract fun getCategoryWithId(categoryId: Long): Category?

    // Insert category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Suspect changes operating thread
    abstract fun insert(entity: Category): Long

    // Insert multiple categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(entities: Collection<Category>)

    // Update
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(entity: Category)

    // Delete
    @Delete
    abstract fun delete(entity: Category): Int

}