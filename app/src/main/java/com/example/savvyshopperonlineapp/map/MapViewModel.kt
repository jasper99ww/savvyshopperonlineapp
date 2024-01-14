package com.example.savvyshopperonlineapp.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import com.example.savvyshopperonlineapp.map.geofence.GeofenceUtils
import com.example.savvyshopperonlineapp.map.geofence.GeofencingHelper
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ShopSpotRepository,
    private val locationService: LocationService
): ViewModel() {

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
        ))

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
    }


    fun onEvent(context: Context, event: MapEvent) {
        when(event) {
            is MapEvent.OnMapLongClick -> {
                viewModelScope.launch {

                    val geocoder = Geocoder(context, Locale.getDefault())

                    val addressName = try {
                        val address = geocoder.getFromLocation(event.latLng.latitude, event.latLng.longitude, 1)
                        address?.firstOrNull()?.getAddressLine(0) ?: "The last added marker"
                    } catch (e: IOException) {
                        "Unknown Location"
                    }

                    val newShopSpot = ShopSpot(
                        event.latLng.latitude,
                        event.latLng.longitude,
                        addressName,
                        radius = "100"
                    )

                    repository.insertShopSpot(newShopSpot)

                    // Create a geofence for the new spot
                    val geofencingHelper = GeofencingHelper(context)
                    val pendingIntent = GeofenceUtils.createGeofencePendingIntent(context, newShopSpot)
                    val radiusInMeters = newShopSpot.radius.toFloatOrNull() ?: 0f // Convert radius to Float
                    geofencingHelper.createGeofenceForShop(newShopSpot, radiusInMeters, pendingIntent)
                }
            }
            is MapEvent.OnInfoWindowLongClick -> {
                viewModelScope.launch {
                    repository.deleteShopSpot(event.spot)
                }
            }
            else -> {}
        }
    }
}
