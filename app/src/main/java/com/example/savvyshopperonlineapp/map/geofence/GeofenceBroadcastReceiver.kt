package com.example.savvyshopperonlineapp.map.geofence

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.savvyshopperonlineapp.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        println("ZACZĘTO geofencebroadcastreceiver")

        val shopName = intent.getStringExtra("shopName")
        val dailyDeal = intent.getStringExtra("dailyDeal")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            // Obsługa błędu
            println("error has occured")
            return
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Tworzenie notyfikacji
            println("tworzymy notfikacje broacast")
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Shop Notification Channel"
                val descriptionText = "Channel for Shop Geofence Notifications"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(
                    "shop_geofence_notification_channel",
                    name,
                    importance
                ).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(context, "shop_geofence_notification_channel")
                .setContentTitle("SavvyShopper Notification")
                .setSmallIcon(R.drawable.grocery_cart)
                .setContentText(if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "Welcome to $shopName! Today's special offer: $dailyDeal!"
                else "Thank you for visiting $shopName! Don't forget to take advantage of our daily promotion: $dailyDeal.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(0, notificationBuilder.build())
        } else {
            println("NIE DZIALA NOTYFIKACJA BROADCASt")
            println("geofence trans is = $geofenceTransition")
        }
    }
}
