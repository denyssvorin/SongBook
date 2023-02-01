package com.example.songbook

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.example.songbook.contract.*
import com.example.songbook.databinding.ActivityMainBinding
import com.example.songbook.fragments.FavoriteFragment
import com.example.songbook.fragments.HomeFragment
import com.example.songbook.fragments.ProfileFragment
import com.example.songbook.fragments.SongsFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.nav_host_fragment_activity_main, HomeFragment())
                .commit()
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)

//        val navView: BottomNavigationView = binding.bottomNavView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_profile
//            )
//        )
        setSupportActionBar(binding.topAppBar)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

/* toolbar icons onClick
            topAppBar.setNavigationOnClickListener {
                // Handle navigation icon press
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        // Handle edit text press
                        true
                    }
                    R.id.add_to_favorite -> {
                        // Handle favorite icon press
                        true
                    }
                    R.id.more -> {
                        // Handle more item (inside overflow menu) press
                        true
                    }
                    else -> false
                }
            }
*/
        binding.bottomNavView.setOnItemSelectedListener { bottomItem ->
            when (bottomItem.itemId) {
                R.id.navigation_home -> {
                    launchFragment(HomeFragment())
                    true
                }
                R.id.navigation_favorite -> {
                    openFragmentFromBottomNav(FavoriteFragment())
                    true
                }
                R.id.navigation_profile -> {
                    openFragmentFromBottomNav(ProfileFragment())
                    true
                }
                else -> false
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun showSongsList(item_name: String) {
        launchFragment(SongsFragment.newInstance(item_name))
    }


    override fun openFragmentFromBottomNav(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, fragment)
            .commit()


    }


    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
//            .addToBackStack(null)
            .replace(R.id.nav_host_fragment_activity_main, fragment)
            .commit()
    }

    private fun updateUi() {
        val fragment = currentFragment

        if (fragment is HasCustomTitle) {
            binding.topAppBar.title = fragment.getTitleRes()
        }
        else {
            binding.topAppBar.title = getString(R.string.app_name)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        if (fragment is HasCustomActions) {
            createCustomToolbarAction(fragment.getCustomAction())
        }
        else {
            binding.topAppBar.menu.clear()
        }
        println("TAG " + supportFragmentManager.backStackEntryCount)
    }

    private fun createCustomToolbarAction(action: CustomAction) {
        binding.topAppBar.menu.clear() // clearing old action if it exists before assigning a new one

        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.BLACK)

        val menuItem = binding.topAppBar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    companion object {
        @JvmStatic private val KEY_RESULT = "RESULT"
    }
}