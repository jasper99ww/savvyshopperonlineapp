package com.example.savvyshopperonlineapp.data

import kotlinx.coroutines.flow.Flow

interface StorageService {
    val items: Flow<List<Item>>
    suspend fun createItem(item: Item)
    suspend fun readItem(itemId: String): Item?
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(itemId: String)
    suspend fun getItemsFilteredByCategory(categoryId: String): Flow<List<Item>>
    suspend fun createSharedList(sharedList: SharedList)
    suspend fun addItemsToSharedList(listId: String, itemIds: List<String>)
    suspend fun shareListWithUser(listId: String, userId: String)
    suspend fun unshareListWithUser(listId: String, userId: String)
}
