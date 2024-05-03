package cz.cvut.fit.poberboh.loc_share.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentMapBinding
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import org.osmdroid.views.MapView

/**
 * Fragment displaying a map view for the home screen.
 * This fragment handles the visibility of the map view when the fragment is resumed or paused.
 */
class MapFragment : LocationFragment<FragmentMapBinding>() {

    /**
     * Called when the fragment is resumed.
     * Sets the map view visibility to VISIBLE.
     */
    override fun onResume() {
        super.onResume()
        mapView.visibility = MapView.VISIBLE
    }

    /**
     * Called when the fragment is paused.
     * Sets the map view visibility to INVISIBLE.
     */
    override fun onPause() {
        super.onPause()
        mapView.visibility = MapView.INVISIBLE
    }

    /**
     * Gets the view model associated with this fragment.
     *
     * @return The view model class for the home screen.
     */
    override fun getViewModel() = HomeViewModel::class.java

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The layout inflater.
     * @param container The view group container.
     * @return An instance of [FragmentMapBinding] representing the inflated layout.
     */
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMapBinding.inflate(inflater, container, false)

    /**
     * Gets the repository associated with this fragment.
     *
     * @return An instance of [BasicRepository] for handling basic operations.
     */
    override fun getFragmentRepository(): BasicRepository {
        val api = remoteDataSource.buildApi(BasicApi::class.java, appPreferences)
        return BasicRepository(api, appPreferences)
    }
}