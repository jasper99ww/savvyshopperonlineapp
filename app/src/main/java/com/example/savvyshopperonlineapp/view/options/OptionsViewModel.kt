package com.example.savvyshopperonlineapp.view.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.database.room.DataStoreManager
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val accountService: AccountService,
    private val dataStoreManager: DataStoreManager
) : SavvyShopperAppViewModel() {

    fun saveSettings(fontSize: Float, fontColor: String) {
        viewModelScope.launch {
            dataStoreManager.saveFontSize(fontSize)
            dataStoreManager.saveFontColor(fontColor)
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    fun onDeleteAccountClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                println("deletion done")
                accountService.deleteAccount(email, password)
            } catch (e: Exception) {
                println("Error - $e")
            }
        }
    }
}

class OptionsViewModelFactory(
    private val accountService: AccountService,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OptionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OptionsViewModel(accountService, dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
