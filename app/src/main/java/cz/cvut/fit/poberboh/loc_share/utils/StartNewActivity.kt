package cz.cvut.fit.poberboh.loc_share.utils

import android.app.Activity
import android.content.Intent

/**
 * Starts a new activity of type [A].
 *
 * @param activity The class of the activity to start.
 */
fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}