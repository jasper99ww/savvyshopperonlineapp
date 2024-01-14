package com.example.savvyshopperonlineapp.map

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun RequestLocationPermissions(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit,
    onLocationPermissionProcessCompleted: () -> Unit,
    showPermissionDialog: MutableState<Boolean>
) {

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var hasReturnedFromSettings by remember { mutableStateOf(false) }

    // Function to check permissions and invoke completion callback
    fun checkPermissions() {
        val foregroundLocationApproved = (
                ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                )
        val backgroundPermissionApproved = (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        ContextCompat.checkSelfPermission(context, ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                )

        if (foregroundLocationApproved && backgroundPermissionApproved) {
            onPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
        onLocationPermissionProcessCompleted()
    }

    // Lifecycle observer to check permissions when app resumes
    val observer = remember {
        object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                if (hasReturnedFromSettings) {
                    checkPermissions()
                    hasReturnedFromSettings = false
                }
            }
        }
    }

    // Register the observer
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Launcher for requesting background location permission
    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                showPermissionDialog.value = true
            } else {
                onPermissionsGranted()
                onLocationPermissionProcessCompleted() // Invoke only if no dialog is shown
            }
        }
    )


    // Launcher for requesting foreground location permission
    val foregroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
            showPermissionDialog.value = true
            onLocationPermissionProcessCompleted()
        }
    )

    // Effect to request permissions when the composable enters the composition
    DisposableEffect(Unit) {
        val foregroundLocationApproved = (
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                )

        if (!foregroundLocationApproved) {
            foregroundLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            checkPermissions()
        }
        onDispose { }
    }

    // Display an alert dialog if the permission dialog flag is set
    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog.value = false
                onLocationPermissionProcessCompleted() // Invoke on dialog dismiss
            },
            title = { Text("Location Permission Required") },
            text = { Text("This app requires 'Allow all the time' location access for certain features. Please enable this in the app settings.") },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        hasReturnedFromSettings = true
                        showPermissionDialog.value = false
                    }
                ) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}