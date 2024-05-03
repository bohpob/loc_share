package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View

/**
 * Extension function to set the visibility of a view.
 * @param isVisible True if the view should be visible, false otherwise.
 */
fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}