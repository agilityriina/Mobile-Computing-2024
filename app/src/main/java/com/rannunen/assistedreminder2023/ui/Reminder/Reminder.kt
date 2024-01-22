package com.rannunen.assistedreminder2023.ui.Reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.data.entity.Reminder
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
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

    // Picking a date
    val pickerCalendar = Calendar.getInstance()

    val pickerYear: Int = pickerCalendar.get(Calendar.YEAR)
    val pickerMonth : Int = pickerCalendar.get(Calendar.MONTH)
    val pickerDay  : Int  = pickerCalendar.get(Calendar.DAY_OF_MONTH)

    // Picking time of day
    val pickerHour: Int = pickerCalendar.get(Calendar.HOUR)
    val pickerMinute: Int = pickerCalendar.get(Calendar.MINUTE)

    val pickerDate = remember { mutableStateOf("") }
    val pickerTime = remember { mutableStateOf("") }

    val pickDate = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            pickerDate.value = "$mDayOfMonth.${mMonth+1}.$mYear"
        },  pickerYear , pickerMonth , pickerDay,
    )

    val pickTime = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, mHour: Int, mMinute: Int ->
            pickerTime.value = "$mHour.$mMinute"
        },  pickerHour, pickerMinute, true
    )

    // Bitmap for the image taken
    var takenPicture by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher for the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imagePreview ->
            takenPicture = imagePreview
        }
    )

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
                        label = {Text(pickerDate.value)},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { pickDate.show() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditCalendar,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Input for time. Maybe change to actual time picker.... and add some verification...
                Row{
                    OutlinedTextField(
                        value = time.value,
                        onValueChange = {time.value = it},
                        label = {Text(pickerTime.value)},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { pickTime.show() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PunchClock,
                                    contentDescription = null
                                )
                            }
                        }
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
                    Text(text = "Add image from phone", color = MaterialTheme.colors.primaryVariant)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    enabled = true,
                    onClick = {cameraLauncher.launch()
                    },
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(text = "Take picture", color = MaterialTheme.colors.primaryVariant)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    // Show the image taken with phone here if it was
                    takenPicture?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Taken image",
                            modifier = Modifier
                                .width(150.dp)
                                .height(150.dp)
                        )
                    }
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
                                    reminderDate = stringToLong(pickerDate.value),
                                    reminderTime = stringToLongTime(pickerTime.value),
                                    reminderCategoryId = getCategoryId(viewState.categories, category.value),
                                    reminderImage = uriImage.value,
                                    reminderCameraImage = bitmapToByteArray(takenPicture)
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
// Convert image to bytearray
fun bitmapToByteArray(map: Bitmap?): ByteArray{
    val stream = ByteArrayOutputStream()
    if (map != null) {
        map.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    return stream.toByteArray()
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