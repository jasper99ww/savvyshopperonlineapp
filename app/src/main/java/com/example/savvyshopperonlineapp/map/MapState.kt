package com.example.savvyshopperonlineapp.map

import android.location.Location
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.google.maps.android.compose.MapProperties

data class MapState(
    val lastKnownLocation: Location?,
    val properties: MapProperties = MapProperties(
        isMyLocationEnabled = true
    ),
    val shopSpots: List<ShopSpot> = emptyList()
)
