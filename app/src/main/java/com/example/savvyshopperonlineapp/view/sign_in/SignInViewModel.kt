package com.example.savvyshopperonlineapp.view.sign_in

import com.example.savvyshopperonlineapp.ITEMS_LIST_SCREEN
import com.example.savvyshopperonlineapp.SIGN_IN_SCREEN
import com.example.savvyshopperonlineapp.SIGN_UP_SCREEN
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : SavvyShopperAppViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.signIn(email.value, password.value)
            openAndPopUp(ITEMS_LIST_SCREEN, SIGN_IN_SCREEN)
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(SIGN_UP_SCREEN, SIGN_IN_SCREEN)
    }
}