package cz.cvut.fit.poberboh.loc_share.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentMapBinding
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapFragment : BaseFragment<HomeViewModel, FragmentMapBinding, BasicRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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