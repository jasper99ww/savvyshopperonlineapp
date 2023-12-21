package com.example.savvyshopperonlineapp.view.detail

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.SPLASH_SCREEN
import com.example.savvyshopperonlineapp.Utils
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.data.Item
import com.example.savvyshopperonlineapp.data.StorageService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : SavvyShopperAppViewModel() {

    var state by mutableStateOf(DetailState())
        private set

    fun initialize(itemId: String, restartApp: (String) -> Unit) {
        launchCatching {
            val loadedItem = storageService.readItem(itemId)
            if (loadedItem != null) {
                state = state.copy(
                    item = loadedItem.itemName,
                    quantity = loadedItem.quantity,
                    price = loadedItem.price,
                    category = Utils.category.find { it.id.toString() == loadedItem.listIdForeignKey } ?: Category(),
                    isUpdatingItem = true,
                    userId = loadedItem.userId
                )
            } else {
                state = state.copy(isUpdatingItem = false)
            }
        }

        observeAuthenticationState(restartApp)
    }

    private fun observeAuthenticationState(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }

    val isFieldNotEmpty: Boolean
        // price is optional, user does not have to provide it
        get() = state.item.isNotEmpty() && state.quantity.isNotEmpty()

    fun onItemChange(newValue: String){
        state = state.copy(item = newValue)
    }

    fun onPriceChange(newValue: String){
        state = state.copy(price = newValue)
    }

    fun onQuantityChange(newValue: String){
        state = state.copy(quantity = newValue)
    }

    fun onCategoryChange(newValue: Category){
        state = state.copy(category = newValue)
    }

    fun addShoppingItem() {
        launchCatching {
            val newItem = Item(
                itemName = state.item,
                quantity = state.quantity,
                price = state.price,
                listIdForeignKey = state.category.id.toString(),
                isChecked = false
            )
            storageService.createItem(newItem)
        }
    }

    fun updateShoppingItem(itemId: String) {
        launchCatching {
            val updatedItem = Item(
                id = itemId,
                itemName = state.item,
                quantity = state.quantity,
                price = state.price,
                listIdForeignKey = state.category.id.toString(),
                isChecked = false,
                userId = state.userId
            )
            storageService.updateItem(updatedItem)
        }
    }
}

data class DetailState(
    val item: String = "",
    val price: String = "",
    val quantity: String = "",
    val isUpdatingItem:Boolean = false,
    val category: Category = Category(),
    val userId: String = ""
)