package cz.cvut.fit.poberboh.loc_share.utils

import androidx.fragment.app.Fragment
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.ui.auth.LoginFragment
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment

fun Fragment.handleApiError(error: Resource.Error, retry: (() -> Unit)? = null) {
    when {
        error.isNetworkError -> requireView().snackbar(
            "Please check your internet connection",
            retry
        )

        error.errorCode == 401 -> {
            when (this) {
                is LoginFragment -> requireView().snackbar("You've entered incorrect email or password")
                else -> (this as BaseFragment<*, *, *>).logout()
            }
        }

        else -> requireView().snackbar(error.errorBody?.string().toString())
    }
}
