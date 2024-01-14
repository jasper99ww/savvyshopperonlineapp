package com.example.savvyshopperonlineapp.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    popUpScreen: () -> Unit
) {

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = true)
    }

    val cameraPositionState = rememberCameraPositionState()

    // Set properties using MapProperties which you can use to recompose the map
    val mapProperties = MapProperties(
        // Only enable if user has accepted location permissions.
        isMyLocationEnabled = viewModel.state.value.lastKnownLocation != null,
    )

    val state = viewModel.state.value

    val showPermissionDialog = remember { mutableStateOf(false) }

    // Handling location permissions
    RequestLocationPermissions(
        onPermissionsGranted = {
            viewModel.getDeviceLocation(LocationServices.getFusedLocationProviderClient(context))
        },
        onPermissionsDenied = {
            // Handle the denial of location permissions if needed
        },
        onLocationPermissionProcessCompleted = {
            // Handle location permission process
        },
        showPermissionDialog = showPermissionDialog
    )

    LaunchedEffect(state.lastKnownLocation) {
        val ss = viewModel.state.value.lastKnownLocation
        state.lastKnownLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15f
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourite shops") },
                navigationIcon = {
                    IconButton(onClick = popUpScreen) {
                        androidx.compose.material3.Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLongClick = {
                    viewModel.onEvent(context, MapEvent.OnMapLongClick(context, it))
                }
            ) {
                viewModel.state.value.shopSpots.forEach { spot ->
                    val latLng = LatLng(spot.lat, spot.lng)
                    val radiusInMeters = spot.radius.toDoubleOrNull() ?: 0.0

                    Marker(
                        state = rememberMarkerState(position = latLng),
                        title = "${spot.name} - ${spot.description}",
                        snippet = "Long click to delete",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                        onInfoWindowLongClick = {
                            viewModel.onEvent(
                                context, MapEvent.OnInfoWindowLongClick(spot)
                            )
                        }
                    )

                    // Adding radius
                    if (radiusInMeters > 0) {
                        Circle(
                            center = latLng,
                            radius = radiusInMeters,
                            fillColor = Color.Blue.copy(alpha = 0.3f),
                            strokeColor = Color.Red,
                            strokeWidth = 2f
                        )
                    }
                }
                }
            }
        }
    }