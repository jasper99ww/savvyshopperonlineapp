package com.example.savvyshopperonlineapp.database.room

import android.app.Application

class SavvyShoppingApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}