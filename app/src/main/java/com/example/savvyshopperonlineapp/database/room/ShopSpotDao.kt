package com.example.savvyshopperonlineapp.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopSpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopSpot(spot: ShopSpotEntity)

    @Update
    suspend fun updateShopSpot(spot: ShopSpotEntity)

    @Delete
    suspend fun deleteShopSpot(spot: ShopSpotEntity)

    @Query("SELECT * FROM shopspotentity")
    fun getShopSpots(): Flow<List<ShopSpotEntity>>

    @Query("SELECT * FROM shopspotentity WHERE id = :shopId")
    suspend fun getShopSpotById(shopId: Int): ShopSpotEntity?
}