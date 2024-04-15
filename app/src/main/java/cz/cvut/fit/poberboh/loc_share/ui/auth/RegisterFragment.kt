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

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {

    private lateinit var registerProgressBar: ProgressBar
    private lateinit var buttonRegister: Button
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editRepeatTextPassword: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeRegisterResponse()
        setupTextChangeListeners()
        setupClickListeners()
    }

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

    private fun setupTextChangeListeners() {
        editRepeatTextPassword.addTextChangedListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            buttonRegister.enable(
                username.isNotEmpty() && password.isNotEmpty() && it.toString().isNotEmpty()
            )
        }
    }

    private fun setupClickListeners() {
        buttonRegister.setOnClickListener { register() }
        buttonLogin.setOnClickListener { findNavController().navigate(R.id.action_register_to_login) }
    }

    private fun register() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val passwordRepeat = editRepeatTextPassword.text.toString().trim()
        if (password == passwordRepeat) {
            viewModel.register(username, password)
        } else {
            Toast.makeText(
                requireContext(),
                viewModel.getPasswordMismatchWarning(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(
            remoteDataSource.buildApi(AuthApi::class.java, appPreferences),
            appPreferences
        )
}