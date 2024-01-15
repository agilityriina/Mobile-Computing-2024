package com.rannunen.assistedreminder2023.data.repository

import com.rannunen.assistedreminder2023.data.database.CategoryDao
import com.rannunen.assistedreminder2023.data.entity.Category
import kotlinx.coroutines.flow.Flow

// Category data repository
class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun categories(): Flow<List<Category>> = categoryDao.categories()
    fun getCategoryWithId(categoryId: Long) : Category? = categoryDao.getCategoryWithId(categoryId)

    // Add category if not exist and return the id
    suspend fun addCategory(category: Category): Long{
        return when (val local = categoryDao.getCategoryWithName(category.name)){
            null -> categoryDao.insert(category) // If category not exist
            else -> local.id
        }
    }
}