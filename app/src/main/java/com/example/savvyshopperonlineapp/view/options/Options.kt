package com.example.savvyshopperonlineapp.view.options

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.savvyshopperonlineapp.database.room.DataStoreManager
import com.example.savvyshopperonlineapp.ui.theme.Shapes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager.getInstance(context) }
    val viewModel: OptionsViewModel = hiltViewModel()

    val currentFontSize by dataStoreManager.getFontSize().collectAsState(initial = 14f)
    val currentFontColorHex by dataStoreManager.getFontColor().collectAsState(initial = "#000000")

    var fontSizeInput: String by remember(currentFontSize) { mutableStateOf(currentFontSize.toString()) }
    var fontColorInput: String by remember(currentFontColorHex) { mutableStateOf(currentFontColorHex) }

    var showRemoveAccountDialog by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Options",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(
                    top = it.calculateTopPadding(),
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                TextField(
                    value = fontSizeInput,
                    onValueChange = { fontSizeInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Preferred font size for item names") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(12.dp))

                TextField(
                    value = fontColorInput,
                    onValueChange = { fontColorInput = it },
                    label = { Text("Preferred font color for item names (hex)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(12.dp))

                Button(
                    onClick = {
                        val fontSize = fontSizeInput.toFloatOrNull() ?: currentFontSize
                        val fontColor = fontColorInput.ifEmpty { currentFontColorHex }
                        viewModel.saveSettings(fontSize, fontColor)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.large
                ) {
                    Text("Save Settings")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onSignOutClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "Remove account",
                    color = Color.Red,
                    modifier = Modifier
                        .clickable { showRemoveAccountDialog = true }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                if (showRemoveAccountDialog) {
                    AlertDialog(
                        title = { Text("Confirm Removal") },
                        text = {
                            Column {
                                Text("Please confirm your email and password to delete your account.")
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = userEmail,
                                    onValueChange = { userEmail = it },
                                    label = { Text("Email") }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = userPassword,
                                    onValueChange = { userPassword = it },
                                    label = { Text("Password") },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showRemoveAccountDialog = false }) {
                                Text("Cancel")
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.onDeleteAccountClick(userEmail, userPassword)
                                showRemoveAccountDialog = false
                            }) {
                                Text("Confirm")
                            }
                        },
                        onDismissRequest = { showRemoveAccountDialog = false }
                    )
                }
            }
        }
    )
}

