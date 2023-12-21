package com.example.savvyshopperonlineapp.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount(email: String, password: String) {
        try {
            val user = Firebase.auth.currentUser!!
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            val userId = user.uid
            Firebase.auth.currentUser!!.delete().await()
            deleteUserDocuments(userId)

        } catch (e: Exception) {
            println("Error during account deletion: ${e.message}")
        }
    }

    suspend fun deleteUserDocuments(userId: String) {
        val firestore = Firebase.firestore

        val itemsCollection = firestore.collection("items")
        itemsCollection.whereEqualTo("userId", userId).get().await().documents.forEach { document ->
            document.reference.delete().await()
        }

        val sharedListsCollection = firestore.collection("sharedLists")
        sharedListsCollection.whereEqualTo("owner", userId).get()
            .await().documents.forEach { document ->
            document.reference.delete().await()
        }

    }

    override suspend fun resetPassword(email: String) {
        try {
            Firebase.auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }


}