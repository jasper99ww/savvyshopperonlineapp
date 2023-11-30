package com.example.savvyshopperonlineapp.viewmodel.signin

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)