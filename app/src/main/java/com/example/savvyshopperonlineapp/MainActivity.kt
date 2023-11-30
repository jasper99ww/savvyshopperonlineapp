package com.example.savvyshopperonlineapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.savvyshopperonlineapp.navigation.NavigationGraph
//import com.example.savvyshopperonlineapp.navigation.SavvyShopperNavigation
import com.example.savvyshopperonlineapp.ui.theme.SavvyShopperOnlineAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val productId = intent?.extras?.getInt("productId") ?: -1

        setContent {
            SavvyShopperOnlineAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph()
//                    SavvyShopperApp(productId)
                }
            }
        }
    }

    @Composable
    fun SavvyShopperApp(productId: Int) {
//        SavvyShopperNavigation(productId)

    }

}
