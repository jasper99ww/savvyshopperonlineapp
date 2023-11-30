package com.example.savvyshopperonlineapp.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.Utils
import com.example.savvyshopperonlineapp.viewmodel.DetailState
import com.example.savvyshopperonlineapp.viewmodel.DetailViewModel
import com.example.savvyshopperonlineapp.viewmodel.DetailViewModelFactory
import com.example.savvyshopperonlineapp.ui.theme.Shapes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: Int,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = viewModel<DetailViewModel>(factory = DetailViewModelFactory(context, id))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            DetailEntry(
                context = context,
                state = viewModel.state,
                onItemChange = viewModel::onItemChange,
                onQuantityChange = viewModel::onQuantityChange,
                onPriceChange = viewModel::onPriceChange,
                onCategoryChange = viewModel::onCategoryChange,
                updateItem = { viewModel.updateShoppingItem(id) },
                saveItem = viewModel::addShoppingItem,
                navigateUp = navigateUp,
                id = id,
                modifier = Modifier.padding(top = it.calculateTopPadding())
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailEntry(
    context: Context,
    state: DetailState,
    onItemChange:(String) -> Unit,
    onQuantityChange:(String) -> Unit,
    onPriceChange:(String) -> Unit,
    onCategoryChange:(Category) -> Unit,
    updateItem:() -> Unit,
    saveItem:() -> Unit,
    navigateUp:() -> Unit,
    id: Int,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Text Field for an item
        TextField(
            value = state.item,
            onValueChange = { onItemChange(it) },
            label = { Text(text = "Item") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = Shapes.large
        )
        Spacer(modifier = Modifier.Companion.size(12.dp))

        // Text Field for a quantity
        TextField(
            value = state.quantity,
            onValueChange = { onQuantityChange(it) },
            label = { Text(text = "Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = Shapes.large
        )
        Spacer(modifier = Modifier.Companion.size(12.dp))

        // Text Field for a price
        TextField(
            value = state.price,
            onValueChange = { onPriceChange(it) },
            label = { Text(text = "Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = Shapes.large
        )

        Spacer(modifier = Modifier.Companion.size(12.dp))

        LazyRow {
            items(Utils.category) { category: Category ->
                CategoryItem(
                    iconRes = category.resId,
                    title = category.title,
                    selected = category == state.category
                ) {
                    onCategoryChange(category)
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        val buttonTitle = if (state.isUpdatingItem) "Update Item"
        else "Add item"
        Button(
            onClick = {
                when (state.isUpdatingItem) {
                    true -> {
                        updateItem.invoke()
                    }

                    false -> {
                        saveItem.invoke()
                    }
                }
                navigateUp.invoke()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.item.isNotEmpty() && state.quantity.isNotEmpty(),
            shape = Shapes.large
        ) {
            Text(text = buttonTitle)
        }
    }
}