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

/**
 * Abstract base class for Fragments in the application.
 * This class provides common functionality such as handling ViewBinding,
 * initializing ViewModel, and providing a RemoteDataSource for network operations.
 *
 * @param BVM The type of ViewModel associated with the Fragment.
 * @param VB The type of ViewBinding associated with the Fragment.
 * @param BR The type of BaseRepository associated with the Fragment.
 */
abstract class BaseFragment<BVM : BaseViewModel, VB : ViewBinding, BR : BaseRepository> :
    Fragment() {
    // Instance of AppPreferences for managing application preferences
    protected lateinit var appPreferences: AppPreferences

    // ViewBinding for the Fragment
    protected lateinit var binding: VB

    // ViewModel associated with the Fragment
    protected lateinit var viewModel: BVM

    // RemoteDataSource for handling network operations
    protected val remoteDataSource = RemoteDataSource()

    /**
     * Called to create the view hierarchy associated with the Fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root View of the inflated layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize AppPreferences
        appPreferences = AppPreferences(requireContext())

        // Inflate the layout and initialize the ViewBinding
        binding = getFragmentBinding(inflater, container)

        // Create ViewModel using ViewModelFactory and ViewModelProvider
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        // Asynchronously initializes AccessToken and RefreshToken
        lifecycleScope.launch {
            appPreferences.accessToken.first()
            appPreferences.refreshToken.first()
        }

        // Return the root view of the inflated layout
        return binding.root
    }

    /**
     * Function to handle logout action.
     * Clears the access token and starts the AuthActivity to log in again.
     */
    fun logout() = lifecycleScope.launch {
        appPreferences.deleteAccessToken()
        requireActivity().startNewActivity(AuthActivity::class.java)
    }

    /**
     * Abstract method to provide the ViewModel class associated with the Fragment.
     *
     * @return The Class object of the ViewModel associated with the Fragment.
     */
    abstract fun getViewModel(): Class<BVM>

    /**
     * Abstract method to initialize the ViewBinding for the Fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @return The initialized ViewBinding for the Fragment.
     */
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Abstract method to provide the Repository class associated with the Fragment.
     *
     * @return The instance of BaseRepository associated with the Fragment.
     */
    abstract fun getFragmentRepository(): BR
}