package com.example.savvyshopperonlineapp.view.load_list

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.savvyshopperonlineapp.data.SharedList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadListsScreen(
    viewModel: LoadListsViewModel,
    popUpScreen: () -> Unit
){
    val sharedLists by viewModel.sharedLists.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Load Lists") },
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
                    "Shared Lists",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(sharedLists) { (list, ownerEmail) ->
                SharedListRow(
                    listName = "List $ownerEmail",
                    onLoad = {
                        viewModel.switchToList(list.id)
                        Toast.makeText(context, "Loaded list: ${list.name}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            item {
                Button(
                    onClick = {
                        viewModel.switchToOwnList()
                        Toast.makeText(context, "Loaded own list", Toast.LENGTH_SHORT).show()
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Green)
                ) {
                    Text("Load Own List")
                }
            }
        }
    }
}

@Composable
fun SharedListRow(listName: String, onLoad: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onLoad),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(listName, modifier = Modifier.weight(1f))
        IconButton(onClick = onLoad) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Load")
        }
    }
}
