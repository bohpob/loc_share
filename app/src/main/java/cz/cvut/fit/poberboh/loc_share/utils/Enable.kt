package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View

/**
 * Enables or disables the view based on the given [enabled] flag.
 * When enabled, the view's alpha is set to 1.0.
 * When disabled, the view's alpha is set to 0.5.
 *
 * @param enabled True to enable the view, false to disable it.
 */
fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}