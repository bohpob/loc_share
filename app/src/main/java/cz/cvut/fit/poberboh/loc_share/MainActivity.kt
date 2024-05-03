package cz.cvut.fit.poberboh.loc_share

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.utils.startNewActivity

/**
 * Main activity class responsible for managing the application's UI and functionality.
 */
class MainActivity : AppCompatActivity() {
    // AppPreferences instance to manage application preferences
    private lateinit var appPreferences: AppPreferences
    // Request code for permissions request
    private val requestPermissionsCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AppPreferences
        appPreferences = AppPreferences(this)

        // Request necessary permissions if not already granted
        requestPermissionsIfNecessary()
    }

    /**
     * Requests necessary permissions if not already granted.
     */
    private fun requestPermissionsIfNecessary() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
        )

        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                requestPermissionsCode
            )
        } else {
            // Proceed to next activity if all permissions are granted
            newActivity()
        }
    }

    /**
     * Handles the results of permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionsCode) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allPermissionsGranted) {
                // Proceed to next activity if all permissions are granted
                newActivity()
            } else {
                // Show permission denied message and finish the activity
                showPermissionDeniedMessageAndFinish()
            }
        }
    }

    /**
     * Shows a toast message for permission denial and finishes the activity after a delay.
     */
    private fun showPermissionDeniedMessageAndFinish() {
        Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2500)
    }

    /**
     * Method to determine and start the appropriate activity based on the access token
     */
    private fun newActivity() {
        appPreferences.accessToken
            .asLiveData()
            .observe(this) {
                val activity =
                    if (it == null) {
                        AuthActivity::class.java
                    } else {
                        HomeActivity::class.java
                    }
                startNewActivity(activity)
            }
    }
}