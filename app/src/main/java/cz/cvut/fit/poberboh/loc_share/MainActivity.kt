package cz.cvut.fit.poberboh.loc_share

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.utils.startNewActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val appPreferences = AppPreferences(this)

        appPreferences.accessToken
            .asLiveData()
            .observe(this) {
                val activity =
                    if (it == null) {
                        AuthActivity::class.java
                    } else {
                        HomeActivity::class.java
                    }
                startNewActivity(activity)
            }
    }
}