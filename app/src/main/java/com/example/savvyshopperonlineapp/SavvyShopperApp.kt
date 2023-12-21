package com.example.savvyshopperonlineapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savvyshopperonlineapp.ui.theme.SavvyShopperOnlineAppTheme
import com.example.savvyshopperonlineapp.view.options.OptionsScreen
import com.example.savvyshopperonlineapp.view.detail.DetailScreen
import com.example.savvyshopperonlineapp.view.forgot_password.ForgotPasswordScreen
import com.example.savvyshopperonlineapp.view.home.HomeScreen
import com.example.savvyshopperonlineapp.view.load_list.LoadListsScreen
import com.example.savvyshopperonlineapp.view.share.ShareListScreen
import com.example.savvyshopperonlineapp.view.sign_in.SignInScreen
import com.example.savvyshopperonlineapp.view.sign_up.SignUpScreen
import com.example.savvyshopperonlineapp.view.splash.SplashScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SavvyShopperApp() {
    SavvyShopperOnlineAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            Scaffold { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    itemsGraph(appState)
                }
            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        SavvyShopperAppState(navController)
    }

fun NavGraphBuilder.itemsGraph(appState: SavvyShopperAppState) {

    composable(LOAD_LISTS_SCREEN) {
        LoadListsScreen(
            viewModel = hiltViewModel(),
            popUpScreen = { appState.popUp() }
        )
    }

    composable(SHARE_LIST_SCREEN) {
        ShareListScreen(
            viewModel = hiltViewModel(),
            popUpScreen = { appState.popUp() }
        )
    }

    composable(ITEMS_LIST_SCREEN) {
        HomeScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(
        route = "$ITEM_SCREEN$ITEM_ID_ARG",
        arguments = listOf(navArgument(ITEM_ID) { defaultValue = ITEM_DEFAULT_ID })
    ) {
        DetailScreen(
            itemId = it.arguments?.getString(ITEM_ID) ?: ITEM_DEFAULT_ID,
            popUpScreen = { appState.popUp() },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            navigateToForgotPassword = { appState.navigate(FORGOT_PASSWORD_SCREEN) }
        )
    }

    composable(FORGOT_PASSWORD_SCREEN) {
            ForgotPasswordScreen(
                onBack = { appState.popUp() }
            )
        }


    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(OPTIONS_SCREEN) {
        OptionsScreen(
            onBack = { appState.popUp() }
        )
    }
}
