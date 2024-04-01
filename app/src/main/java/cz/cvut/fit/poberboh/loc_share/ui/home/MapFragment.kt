package cz.cvut.fit.poberboh.loc_share.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentMapBinding
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.osmdroid.views.MapView

class MapFragment : LocationFragment<FragmentMapBinding>() {

    override fun onResume() {
        super.onResume()
        mapView.visibility = MapView.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mapView.visibility = MapView.INVISIBLE
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMapBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): BasicRepository {
        val token = runBlocking { appPreferences.accessToken.first() }
        val api = remoteDataSource.buildApi(BasicApi::class.java, token)
        return BasicRepository(api, appPreferences)
    }
}