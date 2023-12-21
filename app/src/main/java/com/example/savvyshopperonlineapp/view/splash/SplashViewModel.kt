package com.example.savvyshopperonlineapp.view.splash

import com.example.savvyshopperonlineapp.ITEMS_LIST_SCREEN
import com.example.savvyshopperonlineapp.SIGN_IN_SCREEN
import com.example.savvyshopperonlineapp.SPLASH_SCREEN
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val accountService: AccountService
) : SavvyShopperAppViewModel() {

  fun onAppStart(openAndPopUp: (String, String) -> Unit) {
    if (accountService.hasUser()) openAndPopUp(ITEMS_LIST_SCREEN, SPLASH_SCREEN)
    else openAndPopUp(SIGN_IN_SCREEN, SPLASH_SCREEN)
  }
}
