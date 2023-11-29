package com.example.savvyshopperonlineapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ProductAdditionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.savvyshopper.PRODUCT_ADDED") {
            intent.getStringExtra("productName")?.let { productName ->
                val toastText = "Product $productName successfully added"
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }
}