package cz.cvut.fit.poberboh.loc_share.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import cz.cvut.fit.poberboh.loc_share.AuthActivity
import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.RemoteDataSource
import cz.cvut.fit.poberboh.loc_share.repository.BaseRepository
import cz.cvut.fit.poberboh.loc_share.utils.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseFragment<BVM : BaseViewModel, VB : ViewBinding, BR : BaseRepository> :
    Fragment() {

    protected lateinit var appPreferences: AppPreferences
    protected lateinit var binding: VB
    protected lateinit var viewModel: BVM
    protected val remoteDataSource = RemoteDataSource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appPreferences = AppPreferences(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        lifecycleScope.launch {
            appPreferences.accessToken.first()
            appPreferences.refreshToken.first()
        }
        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        appPreferences.deleteAccessToken()
        requireActivity().startNewActivity(AuthActivity::class.java)
    }

    abstract fun getViewModel(): Class<BVM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun getFragmentRepository(): BR
}