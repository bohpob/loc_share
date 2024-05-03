package cz.cvut.fit.poberboh.loc_share.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import cz.cvut.fit.poberboh.loc_share.R
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentHomeBinding
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.utils.enable
import cz.cvut.fit.poberboh.loc_share.utils.handleApiError
import cz.cvut.fit.poberboh.loc_share.utils.visible

/**
 * Fragment displaying the home screen of the application.
 * This fragment handles the user interface elements and interactions on the home screen.
 */
class HomeFragment : LocationFragment<FragmentHomeBinding>() {
    // AutoCompleteTextView for selecting a category
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    // Button for toggling the share location feature
    private lateinit var buttonToggle: Button
    // Button for logging out of the application
    private lateinit var buttonLogout: Button
    // Note input field for entering additional information
    private lateinit var editTextCenter: EditText
    private lateinit var textInputLayout: TextInputLayout
    // Progress bar for indicating loading state
    private lateinit var homeProgressBar: ProgressBar

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
        // Observe the username and update the UI accordingly
        observeUsername()
        // Observe the incident and handle the response accordingly
        observeIncident()
        // Observe the stop share response and handle the response accordingly
        observeStop()
        // Observe the button state and update the UI accordingly
        observeButtonToggle()
        // Setup the listeners for the UI elements
        setupListeners()
        // Setup the AutoCompleteTextView for selecting a category
        setupAutoCompleteTextView()
    }

    /**
     * Initializes the view elements for the fragment.
     */
    private fun setupViews() {
        autoCompleteTextView = binding.autoComplete
        editTextCenter = binding.editTextCenter
        textInputLayout = binding.textInputLayout
        homeProgressBar = binding.homeFragmentProgressBar
        buttonLogout = binding.buttonLogout
        buttonToggle = binding.buttonToggle
        homeProgressBar.visible(false)
    }

    /**
     * Observes the username and updates the UI accordingly.
     */
    private fun observeUsername() {
        viewModel.getUsername()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            homeProgressBar.visible(user is Resource.Loading)
            when (user) {
                is Resource.Success -> updateUsername(user.data)
                is Resource.Loading -> homeProgressBar.visible(true)
                is Resource.Error -> handleApiError(user) { viewModel.getUsername() }
            }
        }
    }

    /**
     * Observes the incident and handles the response accordingly.
     */
    private fun observeIncident() {
        viewModel.incident.observe(viewLifecycleOwner) { incident ->
            when (incident) {
                is Resource.Success -> viewModel.handleIncident(incident.data.id)
                is Resource.Error -> handleApiError(incident) { viewModel.createIncident() }
                else -> {}
            }
        }
    }

    /**
     * Observes the stop share response and handles the response accordingly.
     */
    private fun observeStop() {
        viewModel.stop.observe(viewLifecycleOwner) { stop ->
            when (stop) {
                is Resource.Error -> handleApiError(stop) { viewModel.stopShare() }
                else -> {}
            }
        }
    }

    /**
     * Observes the button state and updates the UI accordingly.
     */
    private fun observeButtonToggle() {
        buttonToggle.enable(false)

        viewModel.button.observe(viewLifecycleOwner) { button ->
            editTextCenter.enable(button.first)
            textInputLayout.enable(button.first)
            autoCompleteTextView.enable(button.first)

            buttonToggle.setBackgroundColor(button.second)
            buttonToggle.text = button.third
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            buttonToggle.enable(!category.isNullOrBlank())
        }
    }

    /**
     * Sets up the listeners for the UI elements.
     */
    private fun setupListeners() {
        editTextCenter.addTextChangedListener { text -> viewModel.setEnteredText(text.toString()) }
        buttonToggle.setOnClickListener { viewModel.toggleButton() }
        buttonLogout.setOnClickListener {
            viewModel.logout()
            logout()
        }
    }

    /**
     * Sets up the AutoCompleteTextView for selecting a category.
     */
    private fun setupAutoCompleteTextView() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            autoCompleteTextView.setAdapter(adapter)
        }

        autoCompleteTextView.setText(viewModel.selectedCategory.value, false)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = viewModel.categories.value?.get(position)
            selectedCategory?.let {
                viewModel.setSelectedCategory(it)
            }
        }
    }

    /**
     * Updates the username in the UI.
     *
     * @param userResponse The response containing the user information.
     */
    @SuppressLint("SetTextI18n")
    private fun updateUsername(userResponse: UserResponse) {
        with(binding) {
            textUsername.text = viewModel.getUsernameForm() + " " + userResponse.username.toString()
        }
    }

    /**
     * Get the ViewModel associated with this fragment.
     * @return The ViewModel class.
     */
    override fun getViewModel() = HomeViewModel::class.java

    /**
     * Inflate the binding layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @return The binding object for the fragment's layout.
     */
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    /**
     * Get the repository associated with this fragment.
     * @return The repository object.
     */
    override fun getFragmentRepository(): BasicRepository {
        val api = remoteDataSource.buildApi(BasicApi::class.java, appPreferences)
        return BasicRepository(api, appPreferences)
    }
}