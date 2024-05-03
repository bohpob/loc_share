package cz.cvut.fit.poberboh.loc_share

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fit.poberboh.loc_share.databinding.ActivityAuthBinding

/**
 * AuthActivity:
 * - Represents the authentication activity of the application.
 */
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}