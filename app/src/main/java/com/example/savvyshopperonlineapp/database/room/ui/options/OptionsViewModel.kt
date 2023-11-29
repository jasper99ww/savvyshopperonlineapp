package com.example.savvyshopperonlineapp.database.room.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OptionsViewModel (
    private val dataStoreManager: DataStoreManager
):ViewModel() {

    fun saveSettings(fontSize: Float, fontColor: String) {
        viewModelScope.launch {
            dataStoreManager.saveFontSize(fontSize)
            dataStoreManager.saveFontColor(fontColor)
        }
    }
}

class OptionsViewModelFactory(private val dataStoreManager: DataStoreManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OptionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OptionsViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
