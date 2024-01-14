package com.example.savvyshopperonlineapp.map.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofencingHelper(private val context: Context) {

    private val geofencingClient = LocationServices.getGeofencingClient(context)

    @SuppressLint("MissingPermission")
    fun createGeofenceForShop(shop: ShopSpot, radius: Float, pendingIntent: PendingIntent) {

        val geofence = Geofence.Builder()
            .setRequestId(shop.id.toString())
            .setCircularRegion(shop.lat, shop.lng, radius)
            .setExpirationDuration(60*60*1000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
}
