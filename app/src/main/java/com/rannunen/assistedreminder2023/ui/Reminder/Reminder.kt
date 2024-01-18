package com.rannunen.assistedreminder2023.ui.Reminder

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

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
    val date = rememberSaveable{mutableStateOf("")}
    val time = rememberSaveable{mutableStateOf("")}
    val uriImage = rememberSaveable{mutableStateOf("")}
    val context = LocalContext.current

// Registers a photo picker activity launcher in single-select mode. Save URI to database
    val pickMedia = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            // Give permissions to load image from this uri on relaunch
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION )
            uriImage.value = uri.toString()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


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
                // Input for reminder title
                OutlinedTextField(
                    value = title.value,
                    onValueChange = {title.value = it},
                    label = {Text("Title")},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                // Input for description
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
                // Input for date.  Maybe change to actual date picker.... and add some verification...
                Row{
                    OutlinedTextField(
                        value = date.value,
                        onValueChange = {date.value = it},
                        label = {Text("Date. Example: 01.01.2001")},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Input for time. Maybe change to actual time picker.... and add some verification...
                Row{
                    OutlinedTextField(
                        value = time.value,
                        onValueChange = {time.value = it},
                        label = {Text("Time. Example: 17.30")},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }

                // Select category
                Spacer(modifier = Modifier.height(10.dp))
                CategoryListDropdown(
                    viewState = viewState,
                    category = category
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {
                        pickMedia.launch(arrayOf("image/*"))
                    },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(text = "Add image", color = MaterialTheme.colors.primaryVariant)
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
                                    reminderDate = stringToLong(date.value),
                                    reminderTime = stringToLongTime(time.value),
                                    reminderCategoryId = getCategoryId(viewState.categories, category.value),
                                    reminderImage = uriImage.value
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
fun stringToLong(date: String): Long {
    val returnDate = SimpleDateFormat("dd.MM.yyyy")
    return returnDate.parse(date).time
}

fun stringToLongTime(time: String): Long {
    val returnDate = SimpleDateFormat("HH.mm")
    return returnDate.parse(time).time
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