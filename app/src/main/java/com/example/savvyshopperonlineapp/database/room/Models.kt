package com.example.savvyshopperonlineapp.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingList(
    @ColumnInfo(name = "list_id")
    @PrimaryKey
    val id:Int,
    val name:String
)

@Entity(tableName = "items")
data class Item(
    @ColumnInfo(name = "item_id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemName:String,
    val quantity: String,
    var price: String,
    val listIdForeignKey: Int,
    val isChecked: Boolean
)