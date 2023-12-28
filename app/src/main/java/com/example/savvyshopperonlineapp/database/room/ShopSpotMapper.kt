package com.example.savvyshopperonlineapp.database.room

import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot

fun ShopSpotEntity.toShopSpot(): ShopSpot {
    return ShopSpot(
        lat = lat,
        lng = lng,
        name = name,
        description = description,
        radius = radius,
        id = id
    )
}

fun ShopSpot.toShopSpotEntity(): ShopSpotEntity {
    return ShopSpotEntity(
        lat = lat,
        lng = lng,
        name = name,
        description = description,
        radius = radius,
        id = id
    )
}