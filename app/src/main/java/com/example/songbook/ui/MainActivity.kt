package com.example.songbook.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.songbook.R
import com.example.songbook.databinding.ActivityMainBinding
import com.example.songbook.util.LanguageHelper
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
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupFromSharPref() {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        val editor = sharedPref?.edit()

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
}