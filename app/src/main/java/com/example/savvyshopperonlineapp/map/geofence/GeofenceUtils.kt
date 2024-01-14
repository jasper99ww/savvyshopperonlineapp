package com.example.savvyshopperonlineapp.map.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot

object GeofenceUtils {
    fun createGeofencePendingIntent(context: Context, shopSpot: ShopSpot): PendingIntent {

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            putExtra("shopName", shopSpot.name)
            putExtra("dailyDeal", shopSpot.dailyDeal)
        }

        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

