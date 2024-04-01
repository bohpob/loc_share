package cz.cvut.fit.poberboh.loc_share.ui.home

import android.location.Location
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.viewbinding.ViewBinding
import cz.cvut.fit.poberboh.loc_share.HomeActivity
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

abstract class LocationFragment<B : ViewBinding> :
    BaseFragment<HomeViewModel, B, BasicRepository>() {

    private lateinit var gpsMyLocationProvider: GpsMyLocationProvider
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    protected lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        mapView = (requireActivity() as HomeActivity).mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.setMultiTouchControls(true)

        val compassOverlay = CompassOverlay(
            requireContext(),
            InternalCompassOrientationProvider(requireContext()),
            mapView
        )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)


        gpsMyLocationProvider = GpsMyLocationProvider(requireContext())
        gpsMyLocationProvider.locationUpdateMinTime = 500

        myLocationOverlay = object : MyLocationNewOverlay(gpsMyLocationProvider, mapView) {
            override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
                super.onLocationChanged(location, source)

                location?.let { updatedLocation ->
                    val geoPoint = GeoPoint(updatedLocation.latitude, updatedLocation.longitude)
                    viewModel.updateCurrentLocation(GeoPoint(geoPoint))
                    mapView.controller.setCenter(geoPoint)
                }
            }
        }
        myLocationOverlay.enableMyLocation(gpsMyLocationProvider)
        mapView.overlays.add(myLocationOverlay)

        mapView.visibility = MapView.VISIBLE
    }
}