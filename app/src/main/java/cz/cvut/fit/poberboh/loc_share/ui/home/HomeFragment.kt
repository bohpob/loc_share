package cz.cvut.fit.poberboh.loc_share.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import cz.cvut.fit.poberboh.loc_share.R
import cz.cvut.fit.poberboh.loc_share.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var buttonToggle: Button
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var editTextCenter: EditText
    private lateinit var textInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.updateCategoriesFromResources(requireContext().resources)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        autoCompleteTextView = root.findViewById(R.id.auto_complete)
        editTextCenter = root.findViewById(R.id.editTextCenter)
        textInputLayout = root.findViewById(R.id.textInputLayout)

        setupAutoCompleteTextView()
        setupButtonToggle()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAutoCompleteTextView() {
        homeViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            autoCompleteTextView.setAdapter(adapter)
        }

        autoCompleteTextView.setText(homeViewModel.selectedCategory.value, false)

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = homeViewModel.categories.value?.get(position)
            selectedCategory?.let {
                homeViewModel.setSelectedCategory(it)
            }
        }
    }

    private fun setupButtonToggle() {
        buttonToggle = binding.buttonToggle

        homeViewModel.isButtonActivated.observe(viewLifecycleOwner) { isActivated ->
            updateButtonState(isActivated)
            autoCompleteTextView.isEnabled = !isActivated
            textInputLayout.isEnabled = !isActivated
            editTextCenter.isEnabled = !isActivated
        }

        homeViewModel.isCategorySelected.observe(viewLifecycleOwner) { isCategorySelected ->
            buttonToggle.isEnabled = isCategorySelected
        }

        buttonToggle.setOnClickListener {
            homeViewModel.toggleButton()
        }
    }

    private fun updateButtonState(isActivated: Boolean) {
        if (isActivated) {
            buttonToggle.text = requireContext().getString(R.string.button_deactivation)
            buttonToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            buttonToggle.text = requireContext().getString(R.string.button_activation)
            buttonToggle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }
}