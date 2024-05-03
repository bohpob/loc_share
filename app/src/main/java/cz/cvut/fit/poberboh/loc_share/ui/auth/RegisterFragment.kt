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
import androidx.navigation.fragment.findNavController
import cz.cvut.fit.poberboh.loc_share.AuthActivity
import cz.cvut.fit.poberboh.loc_share.R
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentRegisterBinding
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.api.AuthApi
import cz.cvut.fit.poberboh.loc_share.repository.AuthRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment
import cz.cvut.fit.poberboh.loc_share.utils.enable
import cz.cvut.fit.poberboh.loc_share.utils.handleApiError
import cz.cvut.fit.poberboh.loc_share.utils.startNewActivity
import cz.cvut.fit.poberboh.loc_share.utils.visible

/**
 * Fragment class for the registration screen of the application.
 * This fragment handles the user interface elements and interactions on the registration screen.
 */
class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {
    // ProgressBar for indicating loading state
    private lateinit var registerProgressBar: ProgressBar
    // Button for registering the user
    private lateinit var buttonRegister: Button
    // Button for navigating to the login screen
    private lateinit var buttonLogin: Button
    // EditText field for entering the username
    private lateinit var editTextUsername: EditText
    // EditText field for entering the password
    private lateinit var editTextPassword: EditText
    // EditText field for repeating the password
    private lateinit var editRepeatTextPassword: EditText

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
        // Observe the registration response and handle the response accordingly
        observeRegisterResponse()
        // Set up text change listeners for the EditText fields
        setupTextChangeListeners()
        // Set up the click listeners for the buttons
        setupClickListeners()
    }

    /**
     * Initialize the views for the fragment.
     */
    private fun setupViews() {
        registerProgressBar = binding.registerProgress
        buttonRegister = binding.buttonRegister
        buttonLogin = binding.buttonLogin
        editTextUsername = binding.editTextUsername
        editTextPassword = binding.editTextPassword
        editRepeatTextPassword = binding.editRepeatTextPassword
        binding.registerProgress.visible(false)
        binding.buttonRegister.enable(false)
    }

    /**
     * Observe the registration response and handle the response accordingly.
     */
    private fun observeRegisterResponse() {
        viewModel.registerResponse.observe(viewLifecycleOwner) {
            registerProgressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> requireActivity().startNewActivity(AuthActivity::class.java)
                is Resource.Loading -> registerProgressBar.visible(true)
                is Resource.Error -> handleApiError(it) { register() }
            }
        }
    }

    /**
     * Set up text change listeners for the EditText fields.
     */
    private fun setupTextChangeListeners() {
        val updateButtonState: () -> Unit = {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val repeatPassword = editRepeatTextPassword.text.toString().trim()
            buttonRegister.enable(
                username.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()
            )
        }

        editTextUsername.addTextChangedListener { updateButtonState() }
        editTextPassword.addTextChangedListener { updateButtonState() }
        editRepeatTextPassword.addTextChangedListener { updateButtonState() }
    }

    /**
     * Set up the click listeners for the buttons.
     */
    private fun setupClickListeners() {
        buttonRegister.setOnClickListener { register() }
        buttonLogin.setOnClickListener { findNavController().navigate(R.id.action_register_to_login) }
    }

    /**
     * Register the user.
     */
    private fun register() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val passwordRepeat = editRepeatTextPassword.text.toString().trim()

        if (password == passwordRepeat) {
            val error = viewModel.validateLoginInput(username, password)
            if (error == null) {
                viewModel.register(username, password)
            } else {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                viewModel.getPasswordMismatchWarning(),
                Toast.LENGTH_SHORT
            ).show()
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
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

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