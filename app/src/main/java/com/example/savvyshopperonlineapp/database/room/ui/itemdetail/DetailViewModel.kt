package com.example.savvyshopperonlineapp.database.room.ui.itemdetail

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.Utils
import com.example.savvyshopperonlineapp.database.room.Graph
import com.example.savvyshopperonlineapp.database.room.Item
import com.example.savvyshopperonlineapp.database.room.ShoppingList
import com.example.savvyshopperonlineapp.repository.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailViewModel
constructor(
    private val context: Context,
    private val itemId:Int,
    private val repository: Repository = Graph.repository,
):ViewModel() {

    var state by mutableStateOf(DetailState())
        private set

    init {
        addListItem()
        if (itemId != -1){
            viewModelScope.launch {
                repository.getItemWithList(itemId).collectLatest {
                    state = state.copy(
                        item = it.item.itemName,
                        quantity = it.item.quantity,
                        price = it.item.price,
                        category = Utils.category.find { c ->
                            c.id == it.shoppingList.id
                        } ?: Category()
                    )
                }
            }
        }
    }

    init {
        state = if (itemId != -1) {
            state.copy(isUpdatingItem = true)
        } else {
            state.copy(isUpdatingItem = false)
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

    private fun addListItem(){
        viewModelScope.launch {
            Utils.category.forEach {
                repository.insertList(
                    ShoppingList(
                        id = it.id,
                        name = it.title
                    )
                )
            }
        }
    }

    fun addShoppingItem(){

        viewModelScope.launch {
            val newItem = Item(
                itemName = state.item,
                listIdForeignKey = state.category.id,
                price = state.price,
                quantity = state.quantity,
                isChecked = false
            )

            val newProductId = repository.insertItem(newItem)
            sendProductAddedBroadcast(context, newProductId.toInt(), newItem.itemName)
        }
    }

        private fun sendProductAddedBroadcast(context: Context, productId: Int, productName: String) {
            val intent = Intent("com.example.savvyshopper.PRODUCT_ADDED").apply {
                putExtra("productId", productId)
                putExtra("productName", productName)
                addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            }
//            context.sendBroadcast(intent, )
            context.sendBroadcast(intent, "com.example.savvyshopper.PRODUCT_ADDITION_PERMISSION")
        }

    fun updateShoppingItem(id:Int){
        viewModelScope.launch {
            repository.updateItem(
                Item(
                    itemName = state.item,
                    listIdForeignKey = state.category.id,
                    price = state.price,
                    quantity = state.quantity,
                    isChecked = false,
                    id = id
                )
            )
        }
    }

}

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val context: Context,
    private val itemId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(context, itemId) as T
    }
}

data class DetailState(
    val item: String = "",
    val price: String = "",
    val quantity: String = "",
    val isUpdatingItem:Boolean = false,
    val category: Category = Category()
)