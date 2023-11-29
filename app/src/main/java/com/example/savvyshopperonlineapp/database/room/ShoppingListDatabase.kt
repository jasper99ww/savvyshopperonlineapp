package com.example.savvyshopperonlineapp.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ShoppingList::class,Item::class],
    version = 1,
    exportSchema = false
)
abstract class ShoppingListDatabase:RoomDatabase() {
    abstract fun listDao():ListDao
    abstract fun itemDao():ItemDao

    companion object{
        @Volatile
        var INSTANCE:ShoppingListDatabase? = null
        fun getDatabase(context: Context):ShoppingListDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ShoppingListDatabase::class.java,
                    "shopping_list_database"
                ).build()
                INSTANCE= instance
                return instance
            }
        }
    }
}
