package com.example.savvyshopperonlineapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val auth: AccountService) : StorageService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val items: Flow<List<Item>>
        get() = getCurrentListId().flatMapLatest { listId ->
            if (listId.isNotEmpty()) {
                Firebase.firestore
                    .collection(ITEMS_COLLECTION)
                    .whereEqualTo("listIdForeignKey", listId)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.documents.mapNotNull { document ->
                            document.toObject(Item::class.java)?.copy(id = document.id)
                        }
                    }
            } else {
                flowOf(emptyList())
            }
        }


    private suspend fun getCurrentListIdForCurrentUser(): String {
        val userId = auth.currentUserId
        val userProfile = Firebase.firestore.collection("users").document(userId).get().await()
        return userProfile.getString("currentListId") ?: ""
    }

    private fun getCurrentListId(): Flow<String> {
        val userId = auth.currentUserId
        return Firebase.firestore.collection("users").document(userId).snapshots()
            .map { snapshot -> snapshot.getString("currentListId") ?: "" }
    }

    override suspend fun createItem(item: Item) {
        val listId = getCurrentListIdForCurrentUser()
        val newItem = item.copy(listIdForeignKey = listId)
        val itemRef = Firebase.firestore.collection(ITEMS_COLLECTION).add(newItem).await()

        val sharedListId = getSharedListIdForCurrentUser()
        if (sharedListId != null && listId == sharedListId) {
            addItemsToSharedList(sharedListId, listOf(itemRef.id))
        }
    }

    override suspend fun readItem(itemId: String): Item? {
        return try {
            println("READ ITEM CALLED")
            val snapshot = Firebase.firestore
                .collection(ITEMS_COLLECTION)
                .document(itemId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(Item::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(TAG, "get failed with ", e)
            null
        }
    }

    override suspend fun updateItem(item: Item) {
        Firebase.firestore
            .collection(ITEMS_COLLECTION)
            .document(item.id)
            .set(item, SetOptions.merge())
            .await()
    }

    override suspend fun deleteItem(itemId: String) {
        Firebase.firestore
            .collection(ITEMS_COLLECTION)
            .document(itemId)
            .delete()
            .await()

        val listId = getCurrentListIdForCurrentUser()
        removeItemFromList(itemId, listId)
    }

    private suspend fun removeItemFromList(itemId: String, listId: String) {
        Firebase.firestore.collection("sharedLists").document(listId)
            .update("items", FieldValue.arrayRemove(itemId)).await()
    }

    override suspend fun getItemsFilteredByCategory(categoryId: String): Flow<List<Item>> {
        return auth.currentUser.flatMapLatest { user ->
            if (user != null) {
                Firebase.firestore
                    .collection(ITEMS_COLLECTION)
                    .whereEqualTo("listIdForeignKey", categoryId)
                    .whereEqualTo("userId", user.id)
                    .snapshots()
                    .map { querySnapshot ->
                        querySnapshot.documents.mapNotNull { document ->
                            document.toObject(Item::class.java)?.copy(id = document.id)
                        }
                    }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun createSharedList(sharedList: SharedList) {
        val sharedListRef = Firebase.firestore.collection("sharedLists").document()
        sharedListRef.set(sharedList.copy(id = sharedListRef.id)).await()

        val userId = auth.currentUserId

        Firebase.firestore.collection("users").document(userId)
            .set(mapOf("sharedListId" to sharedListRef.id)).await()
    }

    suspend fun getSharedListIdForCurrentUser(): String? {
        try {
            val userId = Firebase.auth.currentUser?.uid.orEmpty()
            val userProfile = Firebase.firestore.collection("users").document(userId).get().await()
            return userProfile.data?.get("sharedListId") as? String
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching sharedListId: ${e.message}")
            return null
        }
    }

    override suspend fun addItemsToSharedList(listId: String, itemIds: List<String>) {
        val listRef = Firebase.firestore.collection("sharedLists").document(listId)
        listRef.update("items", FieldValue.arrayUnion(*itemIds.toTypedArray())).await()
    }

    override suspend fun shareListWithUser(listId: String, userId: String) {
        val listRef = Firebase.firestore.collection("sharedLists").document(listId)
        listRef.update("sharedWith", FieldValue.arrayUnion(userId)).await()
    }

    override suspend fun unshareListWithUser(listId: String, userId: String) {
        val listRef = Firebase.firestore.collection("sharedLists").document(listId)
        listRef.update("sharedWith", FieldValue.arrayRemove(userId)).await()
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val ITEMS_COLLECTION = "items"
        private const val LIST_ID_FOREIGN_KEY_FIELD = "listIdForeignKey"
        private const val SHOPPING_LIST_COLLECTION = "shopping_lists"
    }
}