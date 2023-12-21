package com.example.savvyshopperonlineapp.view.share

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareListScreen(
    viewModel: ShareListViewModel,
    popUpScreen: () -> Unit
){
    val sharedUsers by viewModel.sharedUsers.collectAsState()
    var userEmailToAdd by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share List") },
                navigationIcon = {
                    IconButton(onClick = popUpScreen) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        LazyColumn {
            item {
                Text(
                    "Shared With",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(sharedUsers) { userEmail ->
                SharedUserItem(
                    userEmail,
                    onDelete = { viewModel.removeSharedUser(userEmail) }
                )
            }
            item {
                OutlinedTextField(
                    value = userEmailToAdd,
                    onValueChange = { userEmailToAdd = it },
                    label = { Text("Add User Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Button(
                    onClick = {
                        viewModel.addSharedUser(userEmailToAdd)
                        userEmailToAdd = "" // Reset the field
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Add Person")
                }
            }
            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}


@Composable
fun SharedUserItem(email: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(email, modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

