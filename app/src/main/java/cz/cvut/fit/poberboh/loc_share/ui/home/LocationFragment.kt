package cz.cvut.fit.poberboh.loc_share.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewbinding.ViewBinding
import cz.cvut.fit.poberboh.loc_share.HomeActivity
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Abstract base class for Fragments in the application that require location updates.
 * This class extends [BaseFragment] and provides functionality to update the user's location on the map at regular intervals.
 *
 * @param VB The type of ViewBinding associated with the Fragment.
 */
abstract class LocationFragment<VB : ViewBinding> :
    BaseFragment<HomeViewModel, VB, BasicRepository>() {
    // MapView to display the user's location
    protected lateinit var mapView: MapView
    // MyLocationNewOverlay to show the user's current location on the map
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    // GpsMyLocationProvider to provide location updates
    private lateinit var myLocationProvider: GpsMyLocationProvider
    // Handler to post delayed updates for location
    private val handler = Handler(Looper.getMainLooper())
    // Interval for location updates in milliseconds
    private val locationUpdateInterval = 500L

    /**
     * Called when the Fragment's view is restored. This is generally tied to Activity.onCreate of the containing
     * Activity's lifecycle.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Initialize views and providers from the parent Activity
        mapView = (requireActivity() as HomeActivity).mapView
        myLocationProvider = (requireActivity() as HomeActivity).myLocationProvider
        myLocationOverlay = (requireActivity() as HomeActivity).myLocationOverlay

        // Post delayed updates for location at regular intervals
        handler.postDelayed(updateLocationRunnable, locationUpdateInterval)
    }

    /**
     * Runnable to update the current location of the user on the map.
     */
    private val updateLocationRunnable = object : Runnable {
        // Update the ViewModel with the current location if available
        override fun run() {
            myLocationOverlay.myLocation?.let {
                viewModel.updateCurrentLocation(GeoPoint(it.latitude, it.longitude))
            }
            // Post the same runnable after the specified interval
            handler.postDelayed(this, locationUpdateInterval)
        }
    }

    /**
     * Called when the Fragment is no longer started. This is generally tied to Activity.onStop of the containing
     * Activity's lifecycle.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateLocationRunnable)
    }
}