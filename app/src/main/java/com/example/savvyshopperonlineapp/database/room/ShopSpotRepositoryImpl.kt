package com.example.savvyshopperonlineapp.database.room

import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopSpotRepositoryImpl(
    private val dao: ShopSpotDao
): ShopSpotRepository {

    override suspend fun insertShopSpot(spot: ShopSpot) {
        dao.insertShopSpot(spot.toShopSpotEntity())
    }

    override suspend fun updateShopSpot(spot: ShopSpot) {
        dao.updateShopSpot(spot.toShopSpotEntity())
    }

    override suspend fun deleteShopSpot(spot: ShopSpot) {
        dao.deleteShopSpot(spot.toShopSpotEntity())
    }

    override fun getShopSpots(): Flow<List<ShopSpot>> {
        return dao.getShopSpots().map { spots ->
        spots.map { it.toShopSpot() }

        }
    }

    override suspend fun getShopSpotById(shopId: Int): ShopSpot? {
        return dao.getShopSpotById(shopId)?.toShopSpot()
    }
}