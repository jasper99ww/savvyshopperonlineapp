package com.example.savvyshopperonlineapp.view.favorite_shops_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import com.example.savvyshopperonlineapp.map.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteShopsListViewModel @Inject constructor(
    private val shopSpotRepository: ShopSpotRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage


    var selectedShopSpot: ShopSpot? = null
        private set

    fun selectShopSpot(shopSpot: ShopSpot) {
        selectedShopSpot = shopSpot
    }

    val favoriteShops = shopSpotRepository.getShopSpots().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // lub SharingStarted.Lazily jeśli nie chcesz używać opóźnienia
        initialValue = emptyList()
    )

    fun showSnackbar(message: String) {
        println("SHOW SNACK BAR URUCHOMIONY")
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun getShopDetails(shopId: Int?): ShopSpot? {
        val ls = favoriteShops.value.firstOrNull { it.id == shopId }
        println("zwrocony ls to $ls")
        return favoriteShops.value.firstOrNull { it.id == shopId }
    }



    fun addFavoriteShop(name: String, description: String, radius: String) {
        locationService.getLastKnownLocation { location ->
            location?.let {
//                val shopSpot = ShopSpot(name, description, radius, it.latitude, it.longitude)
                val shopSpot = ShopSpot(it.latitude, it.longitude, name, description, radius)
                viewModelScope.launch {
                    shopSpotRepository.insertShopSpot(shopSpot)
                }
            }
        }
    }

    fun updateFavoriteShop(shop: ShopSpot) {
        viewModelScope.launch {
            shopSpotRepository.updateShopSpot(shop)
        }
    }

    fun removeFavoriteShop(shop: ShopSpot) {
        viewModelScope.launch {
            shopSpotRepository.deleteShopSpot(shop)
        }
    }
}
