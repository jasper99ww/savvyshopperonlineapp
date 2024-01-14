package com.example.savvyshopperonlineapp.view.load_list

import com.example.savvyshopperonlineapp.data.SharedList
import com.example.savvyshopperonlineapp.data.StorageService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoadListsViewModel @Inject constructor(
    private val storageService: StorageService
) : SavvyShopperAppViewModel() {
    private val _sharedLists = MutableStateFlow<List<Pair<SharedList, String>>>(listOf())
    val sharedLists: StateFlow<List<Pair<SharedList, String>>> = _sharedLists

    init {
        loadSharedLists()
    }

    private fun loadSharedLists() {
        launchCatching {
            val email = Firebase.auth.currentUser?.email.orEmpty()
            val querySnapshot = Firebase.firestore.collection("sharedLists")
                .whereArrayContains("sharedWith", email)
                .get()
                .await()

            val listsWithOwners = querySnapshot.documents.mapNotNull { doc ->
                val sharedList = doc.toObject(SharedList::class.java)
                sharedList?.let { list ->
                    val ownerEmail = getEmailByUserId(list.owner)
                    list to ownerEmail
                }
            }
            _sharedLists.value = listsWithOwners
        }
    }

    private suspend fun getEmailByUserId(userId: String): String {
        try {
            val userSnapshot = Firebase.firestore.collection("users").document(userId).get().await()
            if (userSnapshot.exists()) {
                val email = userSnapshot.getString("email")
                return email ?: "Email not found"
            } else {
                return "User document not found"
            }
        } catch (e: Exception) {
            return "Error fetching email"
        }
    }

    fun switchToList(listId: String) {
        launchCatching {
            val userId = Firebase.auth.currentUser?.uid.orEmpty()

            Firebase.firestore.collection("users").document(userId)
                .update("currentListId", listId).await()
        }
    }

    fun switchToOwnList() {
        launchCatching {
        val userId = Firebase.auth.currentUser?.uid.orEmpty()

        val userProfile = Firebase.firestore.collection("users").document(userId).get().await()
        val ownListId = userProfile.data?.get("ownListId") as? String

        ownListId?.let {
            Firebase.firestore.collection("users").document(userId)
                .update("currentListId", it).await()
        }
        }
    }
}

