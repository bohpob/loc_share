package cz.cvut.fit.poberboh.loc_share

import android.os.Bundle
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

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    internal lateinit var myLocationProvider: GpsMyLocationProvider
    internal lateinit var myLocationOverlay: MyLocationNewOverlay
    internal lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeMapView()
        setupNavigation()
    }

    private fun initializeMapView() {
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

    private fun setupLocationOverlay() {
        myLocationProvider = GpsMyLocationProvider(applicationContext)
        myLocationOverlay = MyLocationNewOverlay(myLocationProvider, mapView)
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.enableMyLocation(myLocationProvider)
        mapView.overlays.add(myLocationOverlay)
    }

    private fun setupMapView() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.setMultiTouchControls(true)
    }

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