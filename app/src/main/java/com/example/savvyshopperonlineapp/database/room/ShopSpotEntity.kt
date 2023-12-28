package com.example.savvyshopperonlineapp.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShopSpotEntity(
    val lat: Double,
    val lng: Double,
    val name: String,
    val description: String,
    val radius: String,
    @PrimaryKey val id: Int? = null
)
