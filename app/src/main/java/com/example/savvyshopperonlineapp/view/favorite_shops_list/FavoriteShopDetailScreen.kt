package com.example.savvyshopperonlineapp.view.favorite_shops_list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.savvyshopperonlineapp.database.room.domain.ShopSpot

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteShopDetailScreen(
    viewModel: FavoriteShopDetailViewModel = hiltViewModel(),
    shopId: Int,
    popUpScreen: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(shopId) {
        viewModel.loadShopSpot(shopId)
    }

    val shop by viewModel.shopSpot.collectAsState()

    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val radius = remember { mutableStateOf("") }

    LaunchedEffect(shop) {
        name.value = shop?.name ?: ""
        description.value = shop?.description ?: ""
        radius.value = shop?.radius?.toString() ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite shop details") },
                navigationIcon = {
                    IconButton(onClick = popUpScreen) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                )
            )
            Spacer(modifier = Modifier.size(12.dp))

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                )
            )
            Spacer(modifier = Modifier.size(12.dp))

            OutlinedTextField(
                value = radius.value,
                onValueChange = { radius.value = it },
                label = { Text("Radius (meteres)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            Button(
                onClick = {
                    if (shop != null) {
                        val updatedShop = ShopSpot(shop!!.lat, shop!!.lng, name.value, description.value, radius.value, "ALL -10%", shop!!.id)
                        viewModel.updateFavoriteShop(updatedShop)
                    } else {
                        viewModel.addFavoriteShop(name.value, description.value, radius.value, context)
                    }
                    popUpScreen() // Return to previous screen
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (shop == null) "Add Shop" else "Update Shop")
            }
        }
    }
}
