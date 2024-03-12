package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.songbook.R
import com.example.songbook.ui.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemeDialogFragment : DialogFragment() {

    private lateinit var themeMap: Map<Int, String>
    private var savedTheme = MODE_NIGHT_FOLLOW_SYSTEM

    private val viewModelSettings: SettingsViewModel by viewModels()

    // initializing in onAttach because these variables requires context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        themeMap = mapOf(
            MODE_NIGHT_FOLLOW_SYSTEM to getString(R.string.use_system_theme),
            MODE_NIGHT_NO to getString(R.string.light),
            MODE_NIGHT_YES to getString(R.string.dark)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        lifecycleScope.launch {
            viewModelSettings.appThemePreferencesFlow.collect { themePreferences ->
                savedTheme = themePreferences.theme
            }
        }

        val selectedItemIndex = themeMap.keys.indexOf(savedTheme)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.theme))
            .setSingleChoiceItems(themeMap.values.toTypedArray(), selectedItemIndex)
            { _, which ->
                savedTheme = themeMap.keys.elementAt(which)

                val themeToSave = savedTheme
                viewModelSettings.saveThemePreferences(themeToSave)
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(KEY_THEME_RESPONSE to themeToSave)
                )
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .create()
    }

    companion object {
        @JvmStatic
        val TAG: String = ThemeDialogFragment::class.java.simpleName

        @JvmStatic
        val KEY_THEME_RESPONSE: String = "KEY_THEME_RESPONSE"

        @JvmStatic
        val REQUEST_KEY: String = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager) {
            val dialogFragment = ThemeDialogFragment()
            dialogFragment.show(manager, TAG)
        }
    }
}