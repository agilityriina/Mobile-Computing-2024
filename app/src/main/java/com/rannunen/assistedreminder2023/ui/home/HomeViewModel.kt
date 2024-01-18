package com.rannunen.assistedreminder2023.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rannunen.assistedreminder2023.Graph
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel (
    private val categoryRepository : CategoryRepository = Graph.categoryRepository
) : ViewModel(){
    private val _state = MutableStateFlow(HomeViewState())
    private val _selectedCategory = MutableStateFlow<Category?>(null)

    //Make states available
    val state: StateFlow<HomeViewState>
        get() = _state

    //Select category from the top category row
    fun onCategorySelected(category: Category){
        _selectedCategory.value = category
    }

    init{
        //Multithreading with coroutine
        viewModelScope.launch{
            combine(
                categoryRepository.categories().onEach { list ->
                    //If no category is selected and list exists -> Pick first category
                    if (list.isNotEmpty() && _selectedCategory.value == null){
                        _selectedCategory.value = list[0]
                    }
                },
                _selectedCategory
            ){  categories, selectedCategory ->
                HomeViewState(
                    categories = categories,
                    selectedCategory = selectedCategory
                )

            }. collect { _state.value = it }
        }
        loadCategoriesFromDb()
    }
    private fun loadCategoriesFromDb() {
        val list = mutableListOf(
            Category(name = "Very Low"),
            Category(name = "Low"),
            Category(name = "Normal"),
            Category(name = "High"),
            Category(name = "Very High"),
        )
        viewModelScope.launch {
            list.forEach { category -> categoryRepository.addCategory(category) }
        }
    }
}

data class HomeViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)