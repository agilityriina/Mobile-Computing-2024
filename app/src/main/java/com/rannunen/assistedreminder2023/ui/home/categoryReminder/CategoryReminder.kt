package com.rannunen.assistedreminder2023.ui.home.categoryReminder


import android.util.Log
import androidx.compose.foundation.background
import com.rannunen.assistedreminder2023.data.entity.Reminder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rannunen.assistedreminder2023.data.database.ReminderToCategory
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.util.viewModelProviderFactoryOf
import java.text.SimpleDateFormat
import java.util.*
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import java.io.InputStream

@Composable
fun CategoryReminder(
    categoryId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: CategoryReminderViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf {CategoryReminderViewModel(categoryId)}
    )
    val viewState by viewModel.state.collectAsState()

    Column(Modifier.fillMaxWidth()){
        ReminderList(
            list = viewState.reminders
        )
    }
}

@Composable
private fun ReminderList(
    list: List<ReminderToCategory>
){
    LazyColumn(
        contentPadding = PaddingValues(2.dp),
        verticalArrangement = Arrangement.Center,
    ){
        items(list){item ->
            ReminderListItem(
                reminder = item.reminder,
                category = item.category,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit,
    category: Category,
    modifier: Modifier = Modifier,
){

    //All of the row clickable
    ConstraintLayout(modifier = Modifier
        .background(MaterialTheme.colors.secondary)
        .clickable { onClick() }) {
        val (divider, reminderImage, reminderTitle, reminderDescription, reminderCategory, icon, date, reminderTime) = createRefs()
        //Set dividers between alarms
        Divider(
            Modifier.constrainAs(divider){
                top.linkTo(parent.top)
                //centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
        Log.d("Show image", reminder.reminderImage)

        val context = LocalContext.current
        val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(reminder.reminderImage))

        AsyncImage(
            model = reminder.reminderImage,
            contentDescription = "Reminder's picture",
            modifier = Modifier.constrainAs(reminderImage){
                width = Dimension.value(150.dp)
                height = Dimension.value(150.dp)
            }
        )
        // Title
        Text(
            text = reminder.reminderTitle,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(reminderTitle){
                linkTo(
                    start = reminderImage.end,
                    end = icon.start,
                    startMargin = 10.dp,
                    endMargin = 4.dp,
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
        // Description
        Text(
            text = reminder.reminderDescription,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(reminderDescription){
                linkTo(
                    start = reminderImage.end,
                    end = icon.start,
                    startMargin = 2.dp,
                    endMargin = 4.dp,
                )
                top.linkTo(reminderTitle.bottom, margin = 0.dp)
                bottom.linkTo(parent.bottom, 5.dp)
                width = Dimension.preferredWrapContent
                centerHorizontallyTo(reminderTitle)
            }
        )
        
        // Duedate
        Text(
            text = formatToString(reminder.reminderDate),
            maxLines = 1,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date){
                linkTo(
                    start = reminderDescription.start,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp
                )
                centerHorizontallyTo(reminderDescription)
                top.linkTo(reminderDescription.bottom, margin = 0.dp)
                bottom.linkTo(parent.bottom, 5.dp)
            }
        )

        // Time
        Text(
            text = formatToStringTime(reminder.reminderTime),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(reminderTime){
                linkTo(
                    start = date.start,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp
                )
                top.linkTo(date.bottom, margin = 0.dp)
                centerHorizontallyTo(reminderDescription)
            }
        )

        // Red icon means the alarm is on, white means it's off
        var iconColor by remember { mutableStateOf(Color.Red) }
        // Alarm-icon.. Maybe add a delete button aswell...
        IconButton(
            // Change color red or white depending if it's clicked
            onClick = {iconColor = if (iconColor == Color.Red){
                    Color.White
            }else {
                Color.Red
            }
                      },
            modifier = Modifier
                .size(55.dp)
                .padding(5.dp)
                .constrainAs(icon) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end)
                }
        ){
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Alarm",
                tint = iconColor
            )

        }
    }
}
private fun formatToString(long: Long): String {
    val date = Date(long)
    val format = SimpleDateFormat("dd MMMM, yyyy")
    println(format.format(date))
    return format.format(date)
}

private fun formatToStringTime(long: Long): String {
    val time = Date(long)
    val format = SimpleDateFormat("HH.mm")
    return format.format(time)
}
