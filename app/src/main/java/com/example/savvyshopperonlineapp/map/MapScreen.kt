package com.example.savvyshopperonlineapp.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.savvyshopperonlineapp.map.clusters.ZoneClusterManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch


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

    RequestLocationPermissions(
        onPermissionsGranted = {
            println("PERMISSON GRANTED")
            viewModel.getDeviceLocation(LocationServices.getFusedLocationProviderClient(context))
        },
        showPermissionDialog = showPermissionDialog
    )


    // Launcher dla uprawnień
//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                viewModel.getDeviceLocation(LocationServices.getFusedLocationProviderClient(context))
//            }
//        }
//    )
//
//    // Prośba o uprawnienia
//    LaunchedEffect(key1 = Unit) {
//        if (ContextCompat.checkSelfPermission(
//                context, ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
//        } else {
//            viewModel.getDeviceLocation(LocationServices.getFusedLocationProviderClient(context))
//        }
//    }

    LaunchedEffect(state.lastKnownLocation) {
        val ss = viewModel.state.value.lastKnownLocation
        println("startowankoo $ss")
        state.lastKnownLocation?.let { location ->
            println("przeszlo location, ${location.latitude} oraz ${location.longitude}")
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
//            properties = viewModel.state.value.properties,
                properties = mapProperties,
                uiSettings = uiSettings,
                onMapLongClick = {
                    viewModel.onEvent(MapEvent.OnMapLongClick(it))
                }
            ) {
                viewModel.state.value.shopSpots.forEach { spot ->
                    println("spot petla - ${spot.name} i lat ${spot.lat} i lng ${spot.lng}")

                    val latLng = LatLng(spot.lat, spot.lng)
                    val radiusInMeters = spot.radius.toDoubleOrNull() ?: 0.0

                    Marker(
                        state = rememberMarkerState(position = latLng),
                        title = "${spot.name} - ${spot.description}",
                        snippet = "Long click to delete",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                        onInfoWindowLongClick = {
                            viewModel.onEvent(
                                MapEvent.OnInfoWindowLongClick(spot)
                            )
                        }
                    )

                    // Dodanie okręgu wokół markera
                    if (radiusInMeters > 0) {
                        Circle(
                            center = latLng,
                            radius = radiusInMeters,
                            fillColor = Color.Blue.copy(alpha = 0.3f), // Półprzezroczysty niebieski
                            strokeColor = Color.Red,
                            strokeWidth = 2f
                        )
                    }
                }
                }
            }
        }
    }

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun MapScreen(
//    viewModel: MapViewModel = hiltViewModel(),
//    popUpScreen: () -> Unit
//) {
//    println("opening MapScreen")
//    val state = viewModel.state.value
//
//    // Set properties using MapProperties which you can use to recompose the map
//    val mapProperties = MapProperties(
//        // Only enable if user has accepted location permissions.
//        isMyLocationEnabled = state.lastKnownLocation != null,
//    )
//
//    val cameraPositionState = rememberCameraPositionState()
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            properties = mapProperties,
//            cameraPositionState = cameraPositionState
//        ) {
//            val context = LocalContext.current
//            val scope = rememberCoroutineScope()
////            MapEffect(state.clusterItems) { map ->
////                if (state.clusterItems.isNotEmpty()) {
////                    val clusterManager = viewModel.setupClusterManager(context, map)
////                    map.setOnCameraIdleListener(clusterManager)
////                    map.setOnMarkerClickListener(clusterManager)
////                    state.clusterItems.forEach { clusterItem ->
////                        map.addPolygon(clusterItem.polygonOptions)
////                    }
////                    map.setOnMapLoadedCallback {
////                        if (state.clusterItems.isNotEmpty()) {
////                            scope.launch {
////                                cameraPositionState.animate(
////                                    update = CameraUpdateFactory.newLatLngBounds(
////                                        viewModel.calculateZoneLatLngBounds(),
////                                        0
////                                    ),
////                                )
////                            }
////                        }
////                    }
////                }
////            }
//
//            // NOTE: Some features of the MarkerInfoWindow don't work currently. See docs:
//            // https://github.com/googlemaps/android-maps-compose#obtaining-access-to-the-raw-googlemap-experimental
//            // So you can use clusters as an alternative to markers.
//            MarkerInfoWindow(
//                state = rememberMarkerState(position = LatLng(52.19039, 20.85586)),
//                snippet = "Some stuff",
//                onClick = {
//                    // This won't work :(
//                    System.out.println("Mitchs_: Cannot be clicked")
//                    true
//                },
//                draggable = true
//            )
//        }
//    }
////    // Center camera to include all the Zones.
////    LaunchedEffect(state.clusterItems) {
////        if (state.clusterItems.isNotEmpty()) {
////            cameraPositionState.animate(
////                update = CameraUpdateFactory.newLatLngBounds(
////                    calculateZoneViewCenter(),
////                    0
////                ),
////            )
////        }
////    }
//}
//
///**
// * If you want to center on a specific location.
// */
//@RequiresApi(34)
//private suspend fun CameraPositionState.centerOnLocation(
//    location: ExerciseRoute.Location
//) = animate(
//    update = CameraUpdateFactory.newLatLngZoom(
//        LatLng(location.latitude, location.longitude),
//        15f
//    ),
//)