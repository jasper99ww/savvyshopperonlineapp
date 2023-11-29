package com.example.savvyshopperonlineapp.database.room.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.savvyshopperonlineapp.Category
import com.example.savvyshopperonlineapp.OptionsActivity
import com.example.savvyshopperonlineapp.ProductAdditionReceiver
import com.example.savvyshopperonlineapp.Utils
import com.example.savvyshopperonlineapp.database.room.Item
import com.example.savvyshopperonlineapp.database.room.ItemsWithList
import com.example.savvyshopperonlineapp.database.room.ui.itemdetail.DetailActivity
import com.example.savvyshopperonlineapp.database.room.ui.options.DataStoreManager
import com.example.savvyshopperonlineapp.ui.theme.Shapes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate:(Int) -> Unit
){
    val context = LocalContext.current
    val dataStoreManager: DataStoreManager = remember { DataStoreManager.getInstance(context) }

    val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
    val homeState = homeViewModel.state

    DisposableEffect(Unit) {
        val receiver = ProductAdditionReceiver()
        val filter = IntentFilter("com.example.savvyshopper.PRODUCT_ADDED")
        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val fontSize by dataStoreManager.getFontSize().collectAsState(initial = 14f)
    val fontColorHex by dataStoreManager.getFontColor().collectAsState(initial = "#000000")
    val fontColor = Color(android.graphics.Color.parseColor(fontColorHex))

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Text(
                            "Savvy Shopper",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val navigateToOptionList = Intent(context, OptionsActivity::class.java)
                        context.startActivity(navigateToOptionList)
                    }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Options")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val detailIntent = Intent(context, DetailActivity::class.java)
                context.startActivity(detailIntent)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(bottom = 80.dp)
            ) {
                item {
                    LazyRow {
                        items(Utils.category) { category: Category ->
                            CategoryItem(
                                iconRes = category.resId,
                                title = category.title,
                                selected = category == homeState.category,
                                onItemClick = {
                                    if (category == homeState.category) {
                                        homeViewModel.onCategoryChange(null) // Odznaczanie kategorii
                                    } else {
                                        homeViewModel.onCategoryChange(category) // Zaznaczanie nowej kategorii
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
                items(homeState.items) { item ->
                    ShoppingItems(
                        item = item,
                        isChecked = item.item.isChecked,
                        onCheckedChange = homeViewModel::onItemCheckedChange,
                        onItemClick = {
                            onNavigate.invoke(item.item.id)
                        },
                        fontSize = fontSize.sp,
                        fontColor = fontColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "Number of products: ${homeState.items.size}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    @DrawableRes iconRes: Int,
    title: String,
    selected:Boolean,
    onItemClick:() -> Unit
) {

    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(
                    1.dp,
                    if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                shape = Shapes.large
            )
            .selectable(
                selected = selected,
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = {}
            )
            .clickable { onItemClick() }
        ,
        shape = Shapes.large
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (selected) MaterialTheme.colorScheme.onPrimary
                else Color.Unspecified
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ShoppingItems(
    item: ItemsWithList,
    isChecked:Boolean,
    onCheckedChange:(Item, Boolean) -> Unit,
    onItemClick: () -> Unit,
    fontSize: TextUnit? = null,
    fontColor: Color? = null
) {
    val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)

    val actualFontSize = fontSize ?: MaterialTheme.typography.headlineLarge.fontSize
    val actualFontColor = fontColor ?: MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick.invoke() }
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.item.itemName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = actualFontSize,
                        color = actualFontColor
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start) // Użyj wrapContentWidth z Alignment.Start
                )
                Text(
                    text = "${item.item.quantity}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }




            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.item.price)
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { homeViewModel.deleteItem(item.item) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete"
                    )
                }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onCheckedChange.invoke(item.item, it) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }



}


