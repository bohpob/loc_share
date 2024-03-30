package cz.cvut.fit.poberboh.loc_share.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
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

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    private lateinit var loginProgressBar: ProgressBar
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeLoginResponse()
        setupTextChangeListeners()
        setupClickListeners()
    }

    private fun setupViews() {
        loginProgressBar = binding.loginProgress
        buttonLogin = binding.buttonLogin
        buttonRegister = binding.buttonRegister
        editTextUsername = binding.editTextUsername
        editTextPassword = binding.editTextPassword
        loginProgressBar.visible(false)
        binding.buttonLogin.enable(false)
    }

    private fun observeLoginResponse() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { token ->
            loginProgressBar.visible(token is Resource.Loading)
            when (token) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveAccessToken(token.data.token!!)
                        requireActivity().startNewActivity(HomeActivity::class.java)
                    }
                }

                is Resource.Failure -> handleApiError(token) { login() }
                is Resource.Loading -> loginProgressBar.visible(true)
            }
        }
    }

    private fun setupTextChangeListeners() {
        editTextPassword.addTextChangedListener {
            val username = editTextUsername.text.toString().trim()
            buttonLogin.enable(username.isNotEmpty() && it.toString().isNotEmpty())

        }
    }

    private fun setupClickListeners() {
        buttonLogin.setOnClickListener { login() }
        buttonRegister.setOnClickListener { findNavController().navigate(R.id.action_login_to_register) }
    }

    private fun login() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        viewModel.login(username, password)
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), appPreferences)
}