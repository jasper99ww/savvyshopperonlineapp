package com.example.savvyshopperonlineapp.database.room.domain.repository

import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import kotlinx.coroutines.flow.Flow

interface ShopSpotRepository {

    suspend fun insertShopSpot(spot: ShopSpot)

    suspend fun updateShopSpot(spot: ShopSpot)

    suspend fun deleteShopSpot(spot: ShopSpot)

    fun getShopSpots(): Flow<List<ShopSpot>>

    suspend fun getShopSpotById(shopId: Int): ShopSpot?

}