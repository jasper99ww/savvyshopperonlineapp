package com.example.savvyshopperonlineapp.view.home

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.FAVORITE_SHOPS_LIST_SCREEN
import com.example.savvyshopperonlineapp.ITEM_DEFAULT_ID
import com.example.savvyshopperonlineapp.ITEM_ID
import com.example.savvyshopperonlineapp.ITEM_SCREEN
import com.example.savvyshopperonlineapp.LOAD_LISTS_SCREEN
import com.example.savvyshopperonlineapp.MAP_SCREEN
import com.example.savvyshopperonlineapp.OPTIONS_SCREEN
import com.example.savvyshopperonlineapp.SHARE_LIST_SCREEN
import com.example.savvyshopperonlineapp.SPLASH_SCREEN
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.data.Item
import com.example.savvyshopperonlineapp.data.ItemsWithList
import com.example.savvyshopperonlineapp.data.ShoppingList
import com.example.savvyshopperonlineapp.data.StorageService
import com.example.savvyshopperonlineapp.map.LocationService
import com.example.savvyshopperonlineapp.map.MapState
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val locationService: LocationService
) : SavvyShopperAppViewModel() {

    private var currentCategory: Category? = null

    var state by mutableStateOf(HomeState())
        private set

    val locationState: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
        )
    )

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

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        locationService.getLastKnownLocation { location ->
            location?.let {
                locationState.value = locationState.value.copy(lastKnownLocation = it)
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

    fun onMapClick(openScreen: (String) -> Unit) {
        openScreen("$MAP_SCREEN")
    }

    fun onFavoriteClick(openScreen: (String) -> Unit) {
        openScreen("$FAVORITE_SHOPS_LIST_SCREEN")
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
