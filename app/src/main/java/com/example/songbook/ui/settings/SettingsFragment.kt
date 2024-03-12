package com.example.songbook.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.songbook.databinding.FragmentSettingsBinding
import com.example.songbook.ui.settings.dialogs.AboutDialogFragment
import com.example.songbook.ui.settings.dialogs.LanguageDialogFragment
import com.example.songbook.ui.settings.dialogs.ThemeDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupLanguageDialogFragmentListener()
        setupThemeDialogFragmentListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            buttonLanguage.setOnClickListener {
                showLanguageDialogFragment()
            }

            buttonTheme.setOnClickListener {
                showThemeDialogFragment()
            }

            buttonAbout.setOnClickListener {
                showAboutDialogFragment()
            }
        }

    }

    private fun showLanguageDialogFragment() {
        LanguageDialogFragment.show(childFragmentManager)
    }

    private fun setupLanguageDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            LanguageDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val language = result.getString(LanguageDialogFragment.KEY_LANGUAGE_RESPONSE) ?: "en"
            viewModel.saveLanguagePreferences(language)
        }
    }

    private fun showThemeDialogFragment() {
        ThemeDialogFragment.show(childFragmentManager)
    }

    private fun setupThemeDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            ThemeDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val theme = result.getInt(ThemeDialogFragment.KEY_THEME_RESPONSE)
            Log.i(TAG, "setupThemeDialogFragmentListener: theme from bundle = $theme")
        }
    }

    private fun showAboutDialogFragment() {
        AboutDialogFragment.show(childFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}