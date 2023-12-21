package com.example.savvyshopperonlineapp.data

import com.google.firebase.firestore.DocumentId

data class ShoppingList(
    @DocumentId val id: String,
    val name: String
)

data class Item (
    @DocumentId val id: String = "",
    val itemName: String = "",
    val quantity: String = "",
    var price: String = "",
    val listIdForeignKey: String = "",
    val isChecked: Boolean = false,
    val userId: String = "",
    val sharedWith: List<String> = listOf()
)

data class ItemsWithList(
    val item: Item,
    val shoppingList: ShoppingList
)

data class SharedList(
    @DocumentId val id: String = "",
    val name: String = "",
    val owner: String = "",
    val items: List<String> = listOf(),
    val sharedWith: List<String> = listOf()
)

