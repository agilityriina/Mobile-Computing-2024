package com.rannunen.assistedreminder2023.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun Settings(
    onBackPress: () -> Unit,

){
    Surface(modifier = Modifier.fillMaxSize() ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top

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
                Text(text = "Settings", color= MaterialTheme.colors.primaryVariant)
            }
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {Text("Username: John Doe")},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {Text("Email: John.DoeExample@gmail.com")},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {Text("Profile description: I like cats.")},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
            )

        }
    }

}