package com.example.savvyshopperonlineapp.view.favorite_shops_list

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot
import com.example.savvyshopperonlineapp.database.room.domain.repository.ShopSpotRepository
import com.example.savvyshopperonlineapp.map.LocationService
import com.example.savvyshopperonlineapp.map.geofence.GeofenceUtils
import com.example.savvyshopperonlineapp.map.geofence.GeofencingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteShopDetailViewModel @Inject constructor(
    private val repository: ShopSpotRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _shopSpot = MutableStateFlow<ShopSpot?>(null)
    val shopSpot: StateFlow<ShopSpot?> = _shopSpot

    fun loadShopSpot(shopId: Int) {
        viewModelScope.launch {
            val shopSpot = repository.getShopSpotById(shopId)
            println("dla shopId zaladowano $shopSpot")
            _shopSpot.value = shopSpot
            println("w viemodelu shop jest ${_shopSpot.value}")
            println("Name of the shop is: ${_shopSpot.value?.name}")
        }
    }

    // Metody aktualizujące pola obiektu ShopSpot
    fun updateShopName(newName: String) {
        _shopSpot.value = _shopSpot.value?.copy(name = newName)
    }

    fun updateShopDescription(newDescription: String) {
        _shopSpot.value = _shopSpot.value?.copy(description = newDescription)
    }

    fun updateShopRadius(newRadius: String) {
        _shopSpot.value = _shopSpot.value?.copy(radius = newRadius)
    }

    fun addFavoriteShop(name: String, description: String, radius: String, context: Context) {

//        val pendingIntent = GeofenceUtils.createGeofencePendingIntent(context)

        println("dodajemy to wszystko $name, $description, $radius")
        locationService.getLastKnownLocation { location ->
            location?.let {
//                val shopSpot = ShopSpot(name, description, radius, it.latitude, it.longitude)
                val shopSpot = ShopSpot(it.latitude, it.longitude, name, description, radius)
                viewModelScope.launch {
                    repository.insertShopSpot(shopSpot)
                    // Po dodaniu sklepu, tworzymy dla niego geofence
                    val geofencingHelper = GeofencingHelper(context)
                    val pendingIntent = GeofenceUtils.createGeofencePendingIntent(context, shopSpot)
                    val radiusInMeters = radius.toFloatOrNull() ?: 0f // Konwersja radius na Float
                    geofencingHelper.createGeofenceForShop(shopSpot, radiusInMeters, pendingIntent)
                }
                }
                println("saved favorite shop $shopSpot")
            }
        }

    fun updateFavoriteShop(shop: ShopSpot) {
        viewModelScope.launch {
            repository.updateShopSpot(shop)
        }
    }
}
