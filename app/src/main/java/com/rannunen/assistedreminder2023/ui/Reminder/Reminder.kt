package com.rannunen.assistedreminder2023.ui.Reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun Reminder(
    onBackPress: () -> Unit,
    viewModel: ReminderViewModel = viewModel()

) {
    val coroutineScope = rememberCoroutineScope()
    val viewState by viewModel.state.collectAsState()
    val title = rememberSaveable{mutableStateOf("")}
    val category = rememberSaveable{mutableStateOf("")}
    val description = rememberSaveable{mutableStateOf("")}
    // val date = rememberSaveable{mutableStateOf("")}
    // val time = rememberSaveable{mutableStateOf("")}

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ){
            TopAppBar {
                IconButton(
                    onClick = onBackPress
                ){
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primaryVariant
                    )
                }
                Text(text = "Reminder", color= MaterialTheme.colors.primaryVariant)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(15.dp)
            ) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = {title.value = it},
                    label = {Text("Title")},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = description.value,
                    onValueChange = {description.value = it},
                    label = {Text("Description")},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown(
                    viewState = viewState,
                    category = category
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row{
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = {Text("Date")},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveReminder(
                                Reminder(
                                    reminderTitle = title.value,
                                    reminderDescription = description.value,
                                    reminderDate = Date().time,
                                    reminderCategoryId = getCategoryId(viewState.categories, category.value)
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(text = "Add reminder", color = MaterialTheme.colors.primaryVariant)
                }
            }
        }
    }
}

private fun getCategoryId(categories: List<Category>, categoryName: String): Long{
    // What name matches the category value and get the id of it
    return categories.first {  category -> category.name == categoryName }.id
}

@Composable
// Composable for the dropdown
private fun CategoryListDropdown(
    viewState: ReminderViewState,
    category: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded){
        Icons.Filled.ArrowDropUp
    }else{
        Icons.Filled.ArrowDropDown
    }

    Column {
        OutlinedTextField(
            value = category.value,
            onValueChange = {category.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Category")},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false},
            modifier = Modifier.fillMaxWidth()
        ){
            // View list of values
            viewState.categories.forEach { selection ->
                DropdownMenuItem(
                    onClick = {
                        category.value = selection.name
                        expanded = false
                    }
                ) {
                    Text(text = selection.name )
                }
            }
        }
    }
}