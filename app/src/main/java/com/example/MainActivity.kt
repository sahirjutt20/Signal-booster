package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.BoosterApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SignalViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SignalViewModel

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Triggers live radio state reload if location permissions got granted,
        // otherwise gracefully stays inside lab simulation mode.
        if (::viewModel.isInitialized) {
            viewModel.refreshActiveNetworkTelemetry()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel = ViewModelProvider(this)[SignalViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BoosterApp(viewModel = viewModel)
            }
        }

        // Request real-world location & signal polling permissions at launch
        requestSignalTelemetryPermissions()
    }

    private fun requestSignalTelemetryPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
        
        val needsPermission = permissions.any {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (needsPermission) {
            permissionLauncher.launch(permissions)
        }
    }
}

