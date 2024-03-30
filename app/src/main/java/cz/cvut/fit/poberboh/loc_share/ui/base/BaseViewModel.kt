package cz.cvut.fit.poberboh.loc_share.ui.base

import androidx.lifecycle.ViewModel
import cz.cvut.fit.poberboh.loc_share.repository.BaseRepository

abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel()