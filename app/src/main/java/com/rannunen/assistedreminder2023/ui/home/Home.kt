package com.rannunen.assistedreminder2023.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.rannunen.assistedreminder2023.R
import com.rannunen.assistedreminder2023.data.entity.Category
import com.rannunen.assistedreminder2023.ui.home.categoryReminder.CategoryReminder

@Composable
fun Home(
    viewModel:HomeViewModel = viewModel(),
    navController: NavController
){
    val viewState by viewModel.state.collectAsState()

    val selectedCategory = viewState.selectedCategory

    // Check if empty
    if (viewState.categories.isNotEmpty() && selectedCategory != null) {
        Surface(modifier = Modifier.fillMaxSize()){
            HomeContent(
                selectedCategory = selectedCategory,
                categories = viewState.categories,
                onCategorySelected = viewModel::onCategorySelected,
                navController = navController
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeContent(
    selectedCategory: Category,
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    navController: NavController,
){
    //Layout
    Scaffold(
        modifier = Modifier.padding(bottom = 25.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navController.navigate(route ="reminder")},
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(all = 20.dp)
            ){
                Icon(
                   imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ){
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
        ){
            //Change opacity
            val appBarColor = MaterialTheme.colors.secondary.copy(alpha = 0.70f)

            HomeAppbar(
                backgroundColor = appBarColor,
                navController = navController
            )

            CategoryTabs(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,

            )

            CategoryReminder(
                categoryId = selectedCategory.id,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}

@Composable
private fun HomeAppbar(
    backgroundColor: Color,
    navController: NavController
){
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.primaryVariant,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        backgroundColor = backgroundColor,
        actions = {
            IconButton( onClick = {}){
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton( onClick = {navController.navigate("settings")}){
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account")
            }
        }
    )
}

@Composable
private fun CategoryTabs(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
){
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 22.dp,
        modifier = Modifier.fillMaxWidth()

    ) {
        //Loop all categories
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index ==selectedIndex,
                onClick = {onCategorySelected(category) }
            ) {
              ChoiceChipContent(
                  text = category.name,
                  selected = index == selectedIndex,
                  modifier = Modifier.padding(horizontal = 3.dp, vertical = 15.dp)
              )
            }
        }
    }
}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
){
    Surface(
        //Change color whether selected or not
        color = when {
            selected -> MaterialTheme.colors.primaryVariant.copy(alpha = 0.7f)
            else -> MaterialTheme.colors.onSurface.copy(alpha= 0.2f)
        },
        contentColor = when {
            selected -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

