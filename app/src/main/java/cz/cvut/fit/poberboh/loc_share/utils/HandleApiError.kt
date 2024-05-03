package cz.cvut.fit.poberboh.loc_share.utils

import androidx.fragment.app.Fragment
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.ui.auth.LoginFragment
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseFragment

/**
 * Handles API errors in the context of a fragment.
 *
 * @param error The [Resource.Error] object representing the API error.
 * @param retry An optional lambda function representing the action to retry the API call.
 */
fun Fragment.handleApiError(error: Resource.Error, retry: (() -> Unit)? = null) {
    when {
        error.isNetworkError -> {
            requireView().snackbar("Please check your internet connection", retry)
        }

        error.errorCode == 401 -> {
            handle401Error()
        }

        error.errorCode == -1 -> {
            handleConnectionError(error)
        }

        else -> {
            handleOtherErrors(error)
        }
    }
}

/**
 * Handles a 401 error response from the API.
 */
private fun Fragment.handle401Error() {
    when (this) {
        is LoginFragment -> requireView().snackbar("You've entered incorrect email or password")
        else -> (this as BaseFragment<*, *, *>).logout()
    }
}

/**
 * Handles a connection error response from the API.
 *
 * @param error The [Resource.Error] object representing the connection error.
 */
private fun Fragment.handleConnectionError(error: Resource.Error) {
    requireView().snackbar(error.errorBody?.string().toString())
    (this as BaseFragment<*, *, *>).logout()
}

/**
 * Handles other types of errors from the API.
 *
 * @param error The [Resource.Error] object representing the error.
 */
private fun Fragment.handleOtherErrors(error: Resource.Error) {
    requireView().snackbar(error.errorBody?.string().toString())
}
