package com.example.songbook.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.songbook.R
import com.example.songbook.databinding.ActivityMainBinding
import com.example.songbook.ui.home.HomeFragment
import com.example.songbook.ui.singlesong.SingleSongFragment
import com.example.songbook.util.LanguageHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        setupFromSharPref()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_settings)
        )
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottom_nav_view.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        // validation for show bottom_menu
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val innerFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

        if (innerFragment is SingleSongFragment) showBottomNavigation()

        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupFromSharPref() {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)

        val language = sharedPref.getInt("language_radio_button", 0)
        setLanguage(language)

        val themeIndex = sharedPref.getInt("theme_radio_button", 0)
        setCustomTheme(themeIndex)

    }

    private fun setLanguage(language: Int) {
        if (language == 0) {
            val desiredLanguage = "en"
            LanguageHelper.changeLanguage(this, desiredLanguage)

        } else if (language == 1) {
            val desiredLanguage = "uk"
            LanguageHelper.changeLanguage(this, desiredLanguage)
        }
    }

    private fun setCustomTheme(themeIndex: Int) {
        when (themeIndex) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun showBottomNavigation() {
        val navBar = this.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        navBar?.visibility = View.VISIBLE
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // handle system back press from singleSongActivity
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val innerFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

            if (innerFragment is SingleSongFragment) {
                showBottomNavigation()
            }
            onSupportNavigateUp()

            // handle back press to exit from app
            val backStackCount = supportFragmentManager.backStackEntryCount
            if (backStackCount == 0 && innerFragment is HomeFragment) {
                finish()
            }
        }
    }
}