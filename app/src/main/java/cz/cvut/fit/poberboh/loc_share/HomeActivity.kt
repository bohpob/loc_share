package cz.cvut.fit.poberboh.loc_share

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.cvut.fit.poberboh.loc_share.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_map
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        hideFragments()
    }

    private fun hideFragments() {
        val fragmentManager = supportFragmentManager
        val homeFragment = fragmentManager.findFragmentById(R.id.navigation_home)
        val mapFragment = fragmentManager.findFragmentById(R.id.navigation_map)

        homeFragment?.viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                fragmentManager.beginTransaction().show(homeFragment).commit()
            }

            override fun onPause(owner: LifecycleOwner) {
                fragmentManager.beginTransaction().hide(homeFragment).commit()
            }
        })

        mapFragment?.viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                fragmentManager.beginTransaction().show(mapFragment).commit()
            }

            override fun onPause(owner: LifecycleOwner) {
                fragmentManager.beginTransaction().hide(mapFragment).commit()
            }
        })
    }
}