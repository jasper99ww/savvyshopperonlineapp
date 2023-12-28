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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestLocationPermissions(
    onPermissionsGranted: () -> Unit,
    showPermissionDialog: MutableState<Boolean>
) {
    val context = LocalContext.current

    // Launcher for requesting background location permission.
    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                // If background permission is not granted, show the dialog.
                showPermissionDialog.value = true
            } else {
                // If background permission is granted, execute the provided callback.
                onPermissionsGranted()
            }
        }
    )

    // Launcher for requesting foreground location permission.
    val foregroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Check if foreground permission is granted.
            if (isGranted) {
                onPermissionsGranted()
            }
            // In any case, show the permission dialog.
            showPermissionDialog.value = true
        }
    )

    // Effect to request permissions when the composable enters the composition.
    DisposableEffect(Unit) {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                )
        val backgroundPermissionApproved = (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        PackageManager.PERMISSION_GRANTED ==
                        ContextCompat.checkSelfPermission(context, ACCESS_BACKGROUND_LOCATION)
                )

        // Request permissions if not already granted.
        if (!foregroundLocationApproved || !backgroundPermissionApproved) {
            foregroundLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        } else {
            onPermissionsGranted()
        }
        onDispose { }
    }

    // Display an alert dialog if the permission dialog flag is set.
    if (showPermissionDialog.value) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog.value = false },
            title = { Text("Location Permission Required") },
            text = { Text("This app requires 'Allow all the time' location access for certain features. Please enable this in the app settings.") },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
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
