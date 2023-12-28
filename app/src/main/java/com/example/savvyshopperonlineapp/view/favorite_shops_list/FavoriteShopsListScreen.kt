package com.example.savvyshopperonlineapp.view.favorite_shops_list

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.map.RequestLocationPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteShopsListScreen(
    viewModel: FavoriteShopsListViewModel = hiltViewModel(),
    navigateToDetailScreen: (Int) -> Unit,
    popUpScreen: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {


    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val showPermissionDialog = remember { mutableStateOf(false) }

    RequestLocationPermissions(
        onPermissionsGranted = {
            // Tutaj możesz wykonać działania po przyznaniu uprawnień
        },
        showPermissionDialog = showPermissionDialog
    )


//    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (!isGranted) {
//                showPermissionDialog = true
//            }
//        }
//    )
//
//    val foregroundLocationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                // Jeśli uprawnienia do lokalizacji w pierwszym planie zostały przyznane, żądaj uprawnień do lokalizacji w tle
//                backgroundLocationPermissionLauncher.launch(ACCESS_BACKGROUND_LOCATION)
//            } else {
//                viewModel.showSnackbar("Location permission in foreground is required.")
//            }
//        }
//    )
//
//    // Sprawdzenie uprawnień i ewentualne uruchomienie launchera
//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            foregroundLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
//        }
//    }
//
//    if (showPermissionDialog) {
//        AlertDialog(
//            onDismissRequest = { showPermissionDialog = false },
//            title = { Text("Location Permission Required") },
//            text = { Text("This app requires 'Allow all the time' location access for certain features. Please enable this in the app settings.") },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                            data = Uri.fromParts("package", context.packageName, null)
//                        }
//                        context.startActivity(intent)
//                    }
//                ) {
//                    Text("Go to Settings")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { showPermissionDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }


//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (!isGranted) {
//                viewModel.showSnackbar("To use maps, enable location in settings.")
//            }
//            // Dodatkowe działania, gdy uprawnienia są przyznane
//        }
//    )
//
//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
//        }
//    }

    val favoriteShops by viewModel.favoriteShops.collectAsState()

    LaunchedEffect(viewModel.snackbarMessage.collectAsState().value) {
        viewModel.snackbarMessage.value?.let { message ->
            scaffoldState.snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage() // Wyczyść wiadomość po wyświetleniu
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
                        onClick = { navigateToDetailScreen(-1) }, // Użyj domyślnego konstruktora lub inicjalizacji, aby przekazać nowy sklep
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Add Shop at Current Location")
                    }
                    Spacer(modifier = Modifier.height(80.dp)) // Dodatkowy Spacer po Buttonie
                }
            }
        }
    }
}
