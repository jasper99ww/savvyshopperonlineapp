package com.example.savvyshopperonlineapp.database.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShopSpotEntity::class],
    version = 1
)
abstract class ShopSpotDatabase: RoomDatabase() {

    abstract val dao: ShopSpotDao
}