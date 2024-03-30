package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}