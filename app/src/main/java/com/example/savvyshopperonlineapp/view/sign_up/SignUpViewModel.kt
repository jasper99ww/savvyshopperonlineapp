package com.example.savvyshopperonlineapp.view.sign_up

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.savvyshopperonlineapp.ITEMS_LIST_SCREEN
import com.example.savvyshopperonlineapp.SIGN_UP_SCREEN
import com.example.savvyshopperonlineapp.data.AccountService
import com.example.savvyshopperonlineapp.data.SharedList
import com.example.savvyshopperonlineapp.data.StorageService
import com.example.savvyshopperonlineapp.view.SavvyShopperAppViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : SavvyShopperAppViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {

            if (password.value != confirmPassword.value) {
                _toastMessage.value = "Passwords do not match"
                return@launchCatching
            }

                  try {
                      accountService.signUp(email.value, password.value)
                      val userId = Firebase.auth.currentUser?.uid.orEmpty()
                      val userEmail = email.value
                      val sharedList = SharedList(owner = userId)
                      val sharedListRef = Firebase.firestore.collection("sharedLists").document()
                      sharedListRef.set(sharedList.copy(id = sharedListRef.id)).await()
                      Firebase.firestore.collection("users").document(userId)
                          .set(
                              mapOf(
                                  "email" to userEmail,
                                  "ownListId" to sharedListRef.id,
                                  "currentListId" to sharedListRef.id
                              )
                          ).await()
                      openAndPopUp(ITEMS_LIST_SCREEN, SIGN_UP_SCREEN)
                  } catch (e: FirebaseAuthWeakPasswordException) {
                      _toastMessage.value = "Password is too weak. It must be at least 6 characters."
                  } catch (e: FirebaseAuthException) {
                      _toastMessage.value = e.message ?: "Error during sign up"
                  }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}