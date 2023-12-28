package com.example.savvyshopperonlineapp.map.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot

object GeofenceUtils {
    fun createGeofencePendingIntent(context: Context, shopSpot: ShopSpot): PendingIntent {
        println("GEOFENCE UTILS - creating geofence pending intent")

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            putExtra("shopName", shopSpot.name)
            putExtra("dailyDeal", shopSpot.dailyDeal)
        }
        // Dodaj FLAG_IMMUTABLE dla Android 12 i nowszych
//        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        } else {
//            PendingIntent.FLAG_UPDATE_CURRENT
//        }
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
