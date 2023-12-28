package com.example.savvyshopperonlineapp.map

import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.google.android.gms.maps.model.LatLng

sealed class MapEvent {
    data class OnMapLongClick(val latLng: LatLng): MapEvent()
    data class OnInfoWindowLongClick(val spot: ShopSpot): MapEvent()
}