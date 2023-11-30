package com.example.savvyshopperonlineapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.database.room.Graph
import com.example.savvyshopperonlineapp.database.room.Item
import com.example.savvyshopperonlineapp.database.room.ItemsWithList
import com.example.savvyshopperonlineapp.repository.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        getItems()
    }

    private fun getItems(){
        viewModelScope.launch {
            repository.getItemsWithList.collectLatest {
                state = state.copy(
                    items = it
                )
            }
        }
    }

    fun deleteItem(item: Item){
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun onCategoryChange(category: Category?) {
        if (category == null) {
            state = state.copy(category = Category())
            getItems()
        } else {
            state = state.copy(category = category)
            filterBy(category.id)
        }
    }


    fun onItemCheckedChange(item: Item, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateItem(
                item = item.copy(isChecked = isChecked)
            )
        }
    }

    private fun filterBy(shoppingListId:Int){
        if (shoppingListId != 10001) {
            viewModelScope.launch {
                repository.getItemsWithListFilteredById(
                    shoppingListId
                ).collectLatest {
                    state = state.copy(items = it)
                }
            }
        } else {
            getItems()
        }
    }

}

data class HomeState(
    val items:List<ItemsWithList> = emptyList(),
    val category: Category = Category(),
    val itemChecked: Boolean = false
)