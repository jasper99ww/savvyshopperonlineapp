package com.example.savvyshopperonlineapp.view.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class ShareListViewModel @Inject constructor(
    private val storageService: StorageService
) : SavvyShopperAppViewModel() {

    private val _sharedUsers = MutableStateFlow<List<String>>(listOf())
    val sharedUsers: StateFlow<List<String>> = _sharedUsers

    private var ownListId: String = ""
    private var currentListId: String = ""

    init {
        initializeListData()
    }

    private fun initializeListData() {
        launchCatching {
            val userId = Firebase.auth.currentUser?.uid.orEmpty()
            val userProfile = Firebase.firestore.collection("users").document(userId).get().await()
            ownListId = userProfile.getString("ownListId") ?: ""
            currentListId = userProfile.getString("currentListId") ?: ownListId

            loadSharedUsers(currentListId)
        }
    }

    private fun loadSharedUsers(listId: String) {
        launchCatching {
            val sharedListDoc = Firebase.firestore.collection("sharedLists").document(listId).get().await()
            val sharedList = sharedListDoc.toObject(SharedList::class.java)
            _sharedUsers.value = sharedList?.sharedWith ?: listOf()
        }
    }

    fun addSharedUser(email: String) {
        launchCatching {
            if (currentListId.isNotEmpty()) {
                storageService.shareListWithUser(currentListId, email)
                _sharedUsers.value = _sharedUsers.value + email
            }
        }
    }

    fun removeSharedUser(email: String) {
        launchCatching {
            if (currentListId.isNotEmpty()) {
                storageService.unshareListWithUser(currentListId, email)
                _sharedUsers.value = _sharedUsers.value - email
            }
        }
    }

}
