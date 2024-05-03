package cz.cvut.fit.poberboh.loc_share

import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.cvut.fit.poberboh.loc_share.databinding.ActivityHomeBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * HomeActivity:
 * - Represents the main activity of the application.
 * - Manages the bottom navigation and associated fragments.
 * - Initializes and manages the map view, including location overlay and compass.
 * - Updates the 5G icon based on telephony information if available.
 */
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    internal lateinit var myLocationProvider: GpsMyLocationProvider
    internal lateinit var myLocationOverlay: MyLocationNewOverlay
    internal lateinit var mapView: MapView

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var fiveGIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components and setup
        fiveGIcon = findViewById(R.id.fiveGIcon)
        fiveGIcon.setImageResource(0)
        setupTelephonyManager()
        loadMapView()
        setupNavigation()
    }

    /**
     * Set up the TelephonyManager for monitoring network information.
     */
    private fun setupTelephonyManager() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        // Register TelephonyCallback for getting network information
        // This is only available on Android 12 (API level 31) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.registerTelephonyCallback(mainExecutor, object : TelephonyCallback(),
                TelephonyCallback.DisplayInfoListener {
                override fun onDisplayInfoChanged(displayInfo: TelephonyDisplayInfo) {
                    updateNetworkIcon(displayInfo.networkType)
                }
            })
        }
    }

    /**
     * Updates the network icon based on the network type.
     * @param state The network type.
     */
    private fun updateNetworkIcon(state: Int) {
        // Determine if the network is 5G
        val is5GNetwork = when (state) {
            TelephonyManager.NETWORK_TYPE_NR,
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO,
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA,
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> true

            else -> false
        }

        // Set the icon image based on the network type
        fiveGIcon.setImageResource(if (is5GNetwork) R.drawable.twotone_5g else 0)
    }

    /**
     * Load the map view and set up the map.
     */
    private fun loadMapView() {
        // Load the configuration settings for the map
        Configuration.getInstance()
            .load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )

        mapView = binding.mapView
        setupLocationOverlay()
        setupMapView()
        setupCompass()
        mapView.visibility = MapView.INVISIBLE
    }

    /**
     * Sets up the bottom navigation with associated fragments.
     */
    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_map
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * Set up the location overlay for the map.
     */
    private fun setupLocationOverlay() {
        myLocationProvider = GpsMyLocationProvider(applicationContext)
        myLocationOverlay = MyLocationNewOverlay(myLocationProvider, mapView)
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.enableMyLocation(myLocationProvider)
        mapView.overlays.add(myLocationOverlay)
    }

    /**
     * Set up the map view.
     */
    private fun setupMapView() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.setMultiTouchControls(true)
    }

    /**
     * Set up the compass overlay for the map.
     */
    private fun setupCompass() {
        val compassOverlay = CompassOverlay(
            applicationContext,
            InternalCompassOrientationProvider(applicationContext),
            mapView
        )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
    }
}