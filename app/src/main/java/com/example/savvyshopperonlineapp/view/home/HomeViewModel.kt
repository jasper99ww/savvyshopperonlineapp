package com.example.savvyshopperonlineapp.view.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.ITEM_DEFAULT_ID
import com.example.savvyshopperonlineapp.ITEM_ID
import com.example.savvyshopperonlineapp.ITEM_SCREEN
import com.example.savvyshopperonlineapp.LOAD_LISTS_SCREEN
import com.example.savvyshopperonlineapp.OPTIONS_SCREEN
import com.example.savvyshopperonlineapp.SHARE_LIST_SCREEN
import com.example.savvyshopperonlineapp.SPLASH_SCREEN
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.data.Item
import com.example.savvyshopperonlineapp.data.ItemsWithList
import com.example.savvyshopperonlineapp.data.ShoppingList
import com.example.savvyshopperonlineapp.data.StorageService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : SavvyShopperAppViewModel() {

    private var currentCategory: Category? = null

    var state by mutableStateOf(HomeState())
        private set

    init {
        getItems()
    }

    fun initialize(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(SPLASH_SCREEN)
            }
        }
    }

    private fun getItems(){
        launchCatching {
            val query = if (currentCategory == null) {
                storageService.items
            } else {
                storageService.getItemsFilteredByCategory(currentCategory!!.id.toString())
            }

            query.collectLatest { itemList ->
                val itemsWithList = itemList.map { item ->
                    ItemsWithList(item, ShoppingList(id = item.listIdForeignKey, name = "Unknown"))
                }
                state = state.copy(itemsWithList = itemsWithList)
            }
        }
    }

    fun onCategoryChange(category: Category?) {
        currentCategory = category
        state = state.copy(category = category ?: Category())
        getItems()
    }

    fun onAddClick(openScreen: (String) -> Unit) {
        openScreen("$ITEM_SCREEN?$ITEM_ID=$ITEM_DEFAULT_ID")
    }

    fun onItemClick(openScreen: (String) -> Unit, item: Item) {
        openScreen("$ITEM_SCREEN?$ITEM_ID=${item.id}")
    }

    fun onLoadListsClick(openScreen: (String) -> Unit) {
        openScreen("$LOAD_LISTS_SCREEN")
    }

    fun openOptionsScreen(openScreen: (String) -> Unit) {
        openScreen("$OPTIONS_SCREEN")
    }

    fun onItemCheckedChange(item: Item, isChecked: Boolean) {
        launchCatching {
            val updatedItem = item.copy(isChecked = isChecked)
            storageService.updateItem(updatedItem)

            val updatedList = state.itemsWithList.map {
                if (it.item.id == item.id) {
                    it.copy(item = updatedItem)
                } else {
                    it
                }
            }
            state = state.copy(itemsWithList = updatedList)
        }
    }

    fun deleteItem(itemId: String) {
        launchCatching {
            storageService.deleteItem(itemId)
        }
    }

    fun openShareListScreen(openScreen: (String) -> Unit) {
        openScreen("$SHARE_LIST_SCREEN")
    }
}


data class HomeState(
    val itemsWithList: List<ItemsWithList> = emptyList(),
    val category: Category = Category(),
    val itemChecked: Boolean = false
)
