package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Extension function to show a Snackbar message associated with the given View.
 *
 * @param message The message to be displayed in the Snackbar.
 * @param action The action to be performed when the Snackbar action is clicked.
 */
fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    // Create a Snackbar with the provided message
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)

    // If an action is provided, set the action button text to "Retry" and assign the provided lambda function to be executed when clicked
    action?.let { snackbar.setAction("Retry") { it() } }

    // Show the Snackbar
    snackbar.show()
}