package com.example.songbook.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFromPreferences()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_settings)
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavView.setupWithNavController(navController)

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

    private fun setupFromPreferences() {
        lifecycleScope.launchWhenCreated {
            viewModel.appThemePreferencesFlow.collect { themePreferences ->
                AppCompatDelegate.setDefaultNightMode(themePreferences.theme)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.appLanguagePreferencesFlow.collect { languagePreferences ->
                changeLanguage(this@MainActivity, languagePreferences.language)
            }
        }

    }

    private fun changeLanguage(context: Context, language: String) {
        val configuration = context.resources.configuration
        val currentLanguage = configuration.locales[0].language

        if (currentLanguage != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = Configuration()
            config.setLocale(locale)

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            restartMainActivity()
        }
    }
    private fun restartMainActivity() {
        this.recreate()
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