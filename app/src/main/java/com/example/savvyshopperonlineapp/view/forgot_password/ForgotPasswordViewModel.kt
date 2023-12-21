package com.example.savvyshopperonlineapp.view.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val accountService: AccountService
) : SavvyShopperAppViewModel()  {

    private val _uiState = MutableStateFlow<String?>(null)
    val uiState: StateFlow<String?> = _uiState

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            _uiState.value = "Please enter your e-mail address"
            return
        }

        viewModelScope.launch {
            try {
                accountService.resetPassword(email)
                _uiState.value = "A link to reset your password was sent to the email address you provided"
            } catch (e: Exception) {
                _uiState.value = "Error: ${e.message}"
            }
        }
    }

    fun clearUiState() {
        _uiState.value = null
    }
}
