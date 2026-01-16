package com.example.myinsta

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myinsta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View binding object to reference UI components
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge layout rendering for this activity
        enableEdgeToEdge()


        // Access SharedPreferences to check if user is logged in
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        // If user is not logged in, redirect to LoginActivity and clear task history
        if(!isLoggedIn){
            val intent = Intent(this, LoginActivity::class.java)
            // Clears past activities and prevents LoginActivity from being stored in history
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        // Inflate layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view to the root of the binding (activity_main layout)
        setContentView(binding.root)

        // Setup navigation host fragment for bottom navigation
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController

        // Link the bottom navigation with NavController for navigation handling
        binding.bottomNavigation.setupWithNavController(navController)

        // Handle window inset padding so UI is not obscured by system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Get the insets for system bars like status bar and navigation bar
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding to the root view based on system bar insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}