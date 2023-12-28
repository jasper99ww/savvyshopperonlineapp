package com.example.savvyshopperonlineapp.map

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ShopSpotRepository,
    private val locationService: LocationService
): ViewModel() {

//    var state by mutableStateOf(MapState())

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
        ))
//    val state: MutableState<MapState> = mutableStateOf(MapState())
    
    init {
        viewModelScope.launch {
            repository.getShopSpots().collectLatest { spots ->
                state.value = state.value.copy(
                    shopSpots = spots
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        locationService.getLastKnownLocation { location ->
            location?.let {
                state.value = state.value.copy(lastKnownLocation = it)
            }
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
//         */
    }

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.OnMapLongClick -> {
                println("krótko kliknietio, a wiec dodaj sklep")
                viewModelScope.launch {
                    repository.insertShopSpot(
                        ShopSpot(
                        event.latLng.latitude,
                        event.latLng.longitude,
                            "last added marker"
                    )
                    )
                }
            }
            is MapEvent.OnInfoWindowLongClick -> {
                println("długo przytrzymano")
                viewModelScope.launch {
                    repository.deleteShopSpot(event.spot)
                }
            }

            else -> {}
        }
    }
}

//@HiltViewModel
//class MapViewModel @Inject constructor(): ViewModel() {
//
//    val state: MutableState<MapState> = mutableStateOf(
//        MapState(
//            lastKnownLocation = null,
//            clusterItems = listOf(
//                ZoneClusterItem(
//                    id = "zone-1",
//                    title = "Zone 1",
//                    snippet = "This is Zone 1.",
//                    polygonOptions = polygonOptions {
//                        add(LatLng(49.105, -122.524))
//                        add(LatLng(49.101, -122.529))
//                        add(LatLng(49.092, -122.501))
//                        add(LatLng(49.1, -122.506))
//                        fillColor(POLYGON_FILL_COLOR)
//                    }
//                ),
//                ZoneClusterItem(
//                    id = "zone-2",
//                    title = "Zone 2",
//                    snippet = "This is Zone 2.",
//                    polygonOptions = polygonOptions {
//                        add(LatLng(49.110, -122.554))
//                        add(LatLng(49.107, -122.559))
//                        add(LatLng(49.103, -122.551))
//                        add(LatLng(49.112, -122.549))
//                        fillColor(POLYGON_FILL_COLOR)
//                    }
//                )
//            )
//        )
//    )
//
//    @SuppressLint("MissingPermission")
//    fun getDeviceLocation(
//        fusedLocationProviderClient: FusedLocationProviderClient
//    ) {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            val locationResult = fusedLocationProviderClient.lastLocation
//            locationResult.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    state.value = state.value.copy(
//                        lastKnownLocation = task.result,
//                    )
//                }
//            }
//        } catch (e: SecurityException) {
//            // Show error or something
//        }
//    }
//
//    fun setupClusterManager(
//        context: Context,
//        map: GoogleMap,
//    ): ZoneClusterManager {
//        val clusterManager = ZoneClusterManager(context, map)
//        clusterManager.addItems(state.value.clusterItems)
//        return clusterManager
//    }
//
//    fun calculateZoneLatLngBounds(): LatLngBounds {
//        // Get all the points from all the polygons and calculate the camera view that will show them all.
//        val latLngs = state.value.clusterItems.map { it.polygonOptions }
//            .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
//        return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
//    }
//
//    companion object {
//        private val POLYGON_FILL_COLOR = Color.parseColor("#ABF44336")
//    }
//}