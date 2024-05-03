package cz.cvut.fit.poberboh.loc_share.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cz.cvut.fit.poberboh.loc_share.HomeActivity
import cz.cvut.fit.poberboh.loc_share.R
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentLoginBinding
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.api.AuthApi
import cz.cvut.fit.poberboh.loc_share.repository.AuthRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment
import cz.cvut.fit.poberboh.loc_share.utils.enable
import cz.cvut.fit.poberboh.loc_share.utils.handleApiError
import cz.cvut.fit.poberboh.loc_share.utils.startNewActivity
import cz.cvut.fit.poberboh.loc_share.utils.visible
import kotlinx.coroutines.launch

/**
 * Fragment class for the login screen of the application.
 * This fragment handles the user interface elements and interactions on the login screen.
 */
class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {
    // ProgressBar for indicating loading state
    private lateinit var loginProgressBar: ProgressBar
    // Button for logging in the user
    private lateinit var buttonLogin: Button
    // Button for navigating to the registration screen
    private lateinit var buttonRegister: Button
    // EditText field for entering the username
    private lateinit var editTextUsername: EditText
    // EditText field for entering the password
    private lateinit var editTextPassword: EditText

    /**
     * Called when the fragment view is created.
     * Initializes the view elements and sets up the fragment.
     *
     * @param view The view associated with the fragment.
     * @param savedInstanceState The saved instance state of the fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the view elements
        setupViews()
        // Observe the login response and handle the response accordingly
        observeLoginResponse()
        // Set up text change listeners for the EditText fields
        setupTextChangeListeners()
        // Set up the click listeners for the buttons
        setupClickListeners()
    }

    /**
     * Set up the views for the fragment.
     */
    private fun setupViews() {
        loginProgressBar = binding.loginProgress
        buttonLogin = binding.buttonLogin
        buttonRegister = binding.buttonRegister
        editTextUsername = binding.editTextUsername
        editTextPassword = binding.editTextPassword
        loginProgressBar.visible(false)
        binding.buttonLogin.enable(false)
    }

    /**
     * Observe the login response and handle the response accordingly.
     */
    private fun observeLoginResponse() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { token ->
            loginProgressBar.visible(token is Resource.Loading)
            when (token) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        // Save the tokens to the app preferences
                        viewModel.saveTokens(
                            token.data.accessToken!!,
                            token.data.refreshToken!!
                        )
                        requireActivity().startNewActivity(HomeActivity::class.java)
                    }
                }

                is Resource.Error -> handleApiError(token) { login() }
                is Resource.Loading -> loginProgressBar.visible(true)
            }
        }
    }

    /**
     * Set up the text change listeners for the EditText fields.
     */
    private fun setupTextChangeListeners() {
        val updateButtonState: () -> Unit = {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            buttonLogin.enable(username.isNotEmpty() && password.isNotEmpty())
        }

        editTextUsername.addTextChangedListener { updateButtonState() }
        editTextPassword.addTextChangedListener { updateButtonState() }
    }

    /**
     * Set up the click listeners for the buttons.
     */
    private fun setupClickListeners() {
        buttonLogin.setOnClickListener { login() }
        buttonRegister.setOnClickListener { findNavController().navigate(R.id.action_login_to_register) }
    }

    /**
     * Function to handle the login action.
     */
    private fun login() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        val error = viewModel.validateLoginInput(username, password)
        if (error == null) {
            viewModel.login(username, password)
        } else {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Get the ViewModel associated with this fragment.
     * @return The ViewModel class.
     */
    override fun getViewModel() = AuthViewModel::class.java

    /**
     * Inflate the binding layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @return The binding object for the fragment's layout.
     */
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    /**
     * Get the repository associated with this fragment.
     * @return The repository object.
     */
    override fun getFragmentRepository() =
        AuthRepository(
            remoteDataSource.buildApi(AuthApi::class.java, appPreferences),
            appPreferences
        )
}