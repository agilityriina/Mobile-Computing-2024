package com.rannunen.assistedreminder2023.ui.Reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rannunen.assistedreminder2023.Graph
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import com.rannunen.assistedreminder2023.data.repository.CategoryRepository
import com.rannunen.assistedreminder2023.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
): ViewModel(){
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun saveReminder(reminder: Reminder): Long{
        return reminderRepository.addReminder(reminder)
    }

    init {
        viewModelScope.launch {
            // Collect gets a list of categories
            categoryRepository.categories().collect{ categories ->
                _state.value = ReminderViewState(categories)
            }
        }
    }


}

data class ReminderViewState(
    val categories: List<Category> = emptyList()
)
