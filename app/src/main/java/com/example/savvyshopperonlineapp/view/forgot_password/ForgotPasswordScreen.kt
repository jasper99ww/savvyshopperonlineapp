package com.example.savvyshopperonlineapp.view.forgot_password

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.savvyshopperonlineapp.data.AccountService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        uiState?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (message.contains("sent")) {
                onBack()
            }
            viewModel.clearUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset password") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("E-mail address") },
                placeholder = { Text("Enter your e-mail address") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.resetPassword(email.value) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset password")
            }
        }
    }
}

