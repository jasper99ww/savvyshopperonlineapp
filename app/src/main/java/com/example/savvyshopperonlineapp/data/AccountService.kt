package com.example.savvyshopperonlineapp.data

import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
    suspend fun deleteAccount(email: String, password: String)
    suspend fun resetPassword(email: String)
}
