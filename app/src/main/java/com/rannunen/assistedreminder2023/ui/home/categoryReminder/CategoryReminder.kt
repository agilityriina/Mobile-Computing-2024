package com.rannunen.assistedreminder2023.ui.home.categoryReminder


import androidx.compose.foundation.background
import com.rannunen.assistedreminder2023.data.entity.Reminder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    ConstraintLayout(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.secondary).clickable { onClick() }) {
        val (divider, reminderTitle, reminderCategory, icon, date) = createRefs()
        //Set dividers between alarms
        Divider(
            Modifier.constrainAs(divider){
                top.linkTo(parent.top)
                //centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
        //title
        Text(
            text = reminder.reminderTitle,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(reminderTitle){
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 2.dp,
                    endMargin = 18.dp,
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
        //category
        Text(
            text = category.name,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.constrainAs(reminderCategory){
                linkTo(
                    start = parent.start,
                    end = icon.start,
                    startMargin = 2.dp,
                    endMargin = 8.dp,
                )
                top.linkTo(reminderTitle.bottom, margin = 5.dp)
                bottom.linkTo(parent.bottom, 10.dp)
                width = Dimension.preferredWrapContent
            }
        )
        
        // Duedate
        Text(
            text = reminder.reminderDate.formatToString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date){
                linkTo(
                    start = reminderCategory.end,
                    end = icon.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp
                )
                centerVerticallyTo(reminderCategory)
            }
        )
        // Red icon means the alarm is on, white means it's off
        var iconColor by remember { mutableStateOf(Color.Red) }
        // Alarm-icon
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
                .constrainAs(icon){
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
private fun Date.toDateString(): String{
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(this)
}

private fun Long.formatToString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))
}