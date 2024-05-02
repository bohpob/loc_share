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

abstract class LocationFragment<VB : ViewBinding> :
    BaseFragment<HomeViewModel, VB, BasicRepository>() {
    protected lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var myLocationProvider: GpsMyLocationProvider
    private val handler = Handler(Looper.getMainLooper())
    private val locationUpdateInterval = 500L

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        mapView = (requireActivity() as HomeActivity).mapView
        myLocationProvider = (requireActivity() as HomeActivity).myLocationProvider
        myLocationOverlay = (requireActivity() as HomeActivity).myLocationOverlay

        handler.postDelayed(updateLocationRunnable, locationUpdateInterval)
    }

    private val updateLocationRunnable = object : Runnable {
        override fun run() {
            myLocationOverlay.myLocation?.let {
                viewModel.updateCurrentLocation(GeoPoint(it.latitude, it.longitude))
            }
            handler.postDelayed(this, locationUpdateInterval)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateLocationRunnable)
    }
}