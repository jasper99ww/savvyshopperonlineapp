package com.example.savvyshopperonlineapp.map

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject

class LocationService @Inject constructor
    (private val fusedLocationProviderClient: FusedLocationProviderClient)
{

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(result: (Location?) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                result(location)
            }
            .addOnFailureListener {
            }
    }
}
