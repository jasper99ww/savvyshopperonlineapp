package com.example.savvyshopperonlineapp.view.favorite_shops_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.savvyshopperonlineapp.map.RequestLocationPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteShopsListScreen(
    viewModel: FavoriteShopsListViewModel = hiltViewModel(),
    navigateToDetailScreen: (Int) -> Unit,
    popUpScreen: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()
    val showPermissionDialog = remember { mutableStateOf(false) }

    RequestLocationPermissions(
        onPermissionsGranted = {
            // Handle granted permissions
        },
        onPermissionsDenied = {
            // Handle the denial of location permissions if needed
        },
        onLocationPermissionProcessCompleted = {
            // Handle location permission process
        },
        showPermissionDialog = showPermissionDialog
    )

    val favoriteShops by viewModel.favoriteShops.collectAsState()

    LaunchedEffect(viewModel.snackbarMessage.collectAsState().value) {
        viewModel.snackbarMessage.value?.let { message ->
            scaffoldState.snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Favorite Shops List") },
                navigationIcon = {
                    IconButton(onClick = popUpScreen) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(favoriteShops) { shop ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                shop.id?.let { navigateToDetailScreen(it) }
                            },
                        backgroundColor = Color.LightGray
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Name: ${shop.name}")
                                Text("Description: ${shop.description}")
                                Text("Radius: ${shop.radius}")
                            }
                            IconButton(onClick = { viewModel.removeFavoriteShop(shop) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = { navigateToDetailScreen(-1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Add Shop at Current Location")
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
