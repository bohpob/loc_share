package cz.cvut.fit.poberboh.loc_share.ui.home

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cz.cvut.fit.poberboh.loc_share.R

class HomeViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _isButtonActivated = savedStateHandle.getLiveData("isButtonActivated", false)
    val isButtonActivated: LiveData<Boolean> = _isButtonActivated

    private val _selectedCategory = savedStateHandle.getLiveData("selectedCategory", "")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _isCategorySelected = savedStateHandle.getLiveData("isCategorySelected", false)
    val isCategorySelected: LiveData<Boolean> = _isCategorySelected

    private val _categories = savedStateHandle.getLiveData<List<String>>("categories")
    val categories: LiveData<List<String>> = _categories

    private val _enteredText = savedStateHandle.getLiveData("enteredText", "")
    val enteredText: LiveData<String> = _enteredText

    fun toggleButton() {
        if (_isCategorySelected.value == true) {
            _isButtonActivated.value = !_isButtonActivated.value!!
        }
    }

    fun setSelectedCategory(category: String) {
        if (!_isButtonActivated.value!!) {
            _selectedCategory.value = category
            _isCategorySelected.value = true
        }
    }

    fun updateCategoriesFromResources(resources: Resources) {
        val categoriesArray = resources.getStringArray(R.array.categories).toList()
        _categories.value = categoriesArray
    }

    fun setEnteredText(text: String) {
        _enteredText.value = text
    }
}