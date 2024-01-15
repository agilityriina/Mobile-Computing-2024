package com.rannunen.assistedreminder2023.ui.home.categoryReminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rannunen.assistedreminder2023.Graph
import com.rannunen.assistedreminder2023.data.database.ReminderToCategory
import com.rannunen.assistedreminder2023.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryReminderViewModel(
    private val categoryId: Long,
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
) : ViewModel(){
    private val _state = MutableStateFlow(CategoryReminderViewState())

    //Make state available
    val state: StateFlow<CategoryReminderViewState>
     get() = _state

    init {
        viewModelScope.launch {
            reminderRepository.remindersInCategory(categoryId).collect {list ->
                _state.value = CategoryReminderViewState(
                    reminders = list
                )
            }
        }
    }
}

data class CategoryReminderViewState(
    val reminders: List<ReminderToCategory> = emptyList()
)