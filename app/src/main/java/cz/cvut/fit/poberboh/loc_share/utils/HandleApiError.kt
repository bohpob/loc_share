package cz.cvut.fit.poberboh.loc_share.utils

import androidx.fragment.app.Fragment
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.ui.auth.LoginFragment
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> requireView().snackbar(
            "Please check your internet connection",
            retry
        )

        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("You've entered incorrect email or password")
            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }

        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}