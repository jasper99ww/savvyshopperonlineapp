package com.example.savvyshopperonlineapp.database.room.domain

data class ShopSpot(
    val lat: Double = 0.00,
    val lng: Double = 0.00,
    var name: String = "",
    var description: String = "",
    var radius: String = "",
    val dailyDeal: String = "ALL -10%",
    val id: Int? = null
)
