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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.osmdroid.views.MapView

class HomeFragment : LocationFragment<FragmentHomeBinding>() {

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var buttonToggle: Button
    private lateinit var buttonLogout: Button
    private lateinit var editTextCenter: EditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var homeProgressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeUsername()
        observeIncident()
        observeStop()
        observeButtonToggle()
        setupListeners()
        setupAutoCompleteTextView()
    }

    override fun onResume() {
        super.onResume()
        mapView.visibility = MapView.INVISIBLE
    }

    private fun setupViews() {
        autoCompleteTextView = binding.autoComplete
        editTextCenter = binding.editTextCenter
        textInputLayout = binding.textInputLayout
        homeProgressBar = binding.homeFragmentProgressBar
        buttonLogout = binding.buttonLogout
        buttonToggle = binding.buttonToggle
        homeProgressBar.visible(false)
    }

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

    private fun observeIncident() {
        viewModel.incident.observe(viewLifecycleOwner) { incident ->
            when (incident) {
                is Resource.Success -> viewModel.handleIncident(incident.data.id)
                is Resource.Error -> handleApiError(incident) { viewModel.createIncident() }
                else -> {}
            }
        }
    }

    private fun observeStop() {
        viewModel.stop.observe(viewLifecycleOwner) { stop ->
            when (stop) {
                is Resource.Success -> viewModel.handleStopShare()
                is Resource.Error -> handleApiError(stop) { viewModel.stopShare() }
                else -> {}
            }
        }
    }

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

    private fun setupListeners() {
        editTextCenter.addTextChangedListener { text -> viewModel.setEnteredText(text.toString()) }
        buttonToggle.setOnClickListener { viewModel.toggleButton() }
        buttonLogout.setOnClickListener {
            viewModel.logout()
            logout()
        }
    }

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

    @SuppressLint("SetTextI18n")
    private fun updateUsername(userResponse: UserResponse) {
        with(binding) {
            textUsername.text = viewModel.getUsernameForm() + " " + userResponse.username.toString()
        }
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): BasicRepository {
        val token = runBlocking { appPreferences.accessToken.first() }
        val api = remoteDataSource.buildApi(BasicApi::class.java, token)
        return BasicRepository(api, appPreferences)
    }
}