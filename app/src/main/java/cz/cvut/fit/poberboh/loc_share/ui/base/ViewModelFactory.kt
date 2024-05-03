package cz.cvut.fit.poberboh.loc_share.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.cvut.fit.poberboh.loc_share.repository.AuthRepository
import cz.cvut.fit.poberboh.loc_share.repository.BaseRepository
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.auth.AuthViewModel
import cz.cvut.fit.poberboh.loc_share.ui.home.HomeViewModel

/**
 * Factory class for creating ViewModels with a provided repository.
 * This class is responsible for instantiating ViewModels with the appropriate repository.
 *
 * @param repository The repository to be associated with the ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new instance of the specified ViewModel class.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return A new instance of the specified ViewModel.
     * @throws IllegalArgumentException if the ViewModel class is not supported.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as BasicRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}