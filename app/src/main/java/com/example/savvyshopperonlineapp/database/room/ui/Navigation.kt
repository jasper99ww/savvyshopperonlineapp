package com.example.savvyshopperonlineapp.database.room.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savvyshopperonlineapp.database.room.ui.itemdetail.DetailScreen

enum class Routes{
    Home,
    Detail
}

@Composable
fun SavvyShopperNavigation(
    productId: Int,
    navHostController: NavHostController = rememberNavController()
) {

    val startDestination = if (productId != -1) {
        "${Routes.Detail.name}?productId=$productId"
    } else {
        Routes.Home.name
    }

    NavHost(navController = navHostController, startDestination = startDestination) {
        composable(route = Routes.Home.name) {
            HomeScreen(onNavigate = { id ->
                navHostController.navigate(route = "${Routes.Detail.name}?id=$id")
            })
        }

        composable(
            route = "${Routes.Detail.name}?productId={productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            navHostController.navigate("${Routes.Home.name}")
            navHostController.navigate(route = "${Routes.Detail.name}?id=$productId")
        }

        composable(
            route = "${Routes.Detail.name}?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val idArg = it.arguments?.getInt("id") ?: -1
            DetailScreen(id = idArg) {
                navHostController.navigateUp()
            }
        }
    }
}
