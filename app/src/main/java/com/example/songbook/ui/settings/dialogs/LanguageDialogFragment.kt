package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.songbook.R
import com.example.songbook.ui.settings.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageDialogFragment : DialogFragment() {

    private lateinit var languageMap: Map<String, String>
    private var savedLanguage = ""

    private val viewModelSettings: SettingsViewModel by viewModels()

    // initializing in onAttach because these variables requires context
    override fun onAttach(context: Context) {
        super.onAttach(context)

        languageMap = mapOf(
            "en" to requireContext().getString(R.string.english),
            "uk" to requireContext().getString(R.string.ukrainian)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        lifecycleScope.launch {
            viewModelSettings.appLanguagePreferencesFlow.collect { value ->
                savedLanguage = value.language
            }
        }

        val selectedItemIndex = languageMap.keys.indexOf(savedLanguage)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.language))
            .setSingleChoiceItems(languageMap.values.toTypedArray(), selectedItemIndex) { dialog, which ->
                savedLanguage = languageMap.keys.elementAt(which)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                val languageToSave = savedLanguage
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_LANGUAGE_RESPONSE to languageToSave)
                )
            }
            .create()
    }


    companion object {
        @JvmStatic
        val TAG: String = LanguageDialogFragment::class.java.simpleName
        @JvmStatic
        val KEY_LANGUAGE_RESPONSE: String = "KEY_LANGUAGE_RESPONSE"

        @JvmStatic
        val REQUEST_KEY: String = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager) {
            val dialogFragment = LanguageDialogFragment()
            dialogFragment.show(manager, TAG)
        }
    }
}