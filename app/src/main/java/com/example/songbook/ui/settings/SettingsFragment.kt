package com.example.songbook.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.fragment.app.Fragment
import com.example.songbook.R
import com.example.songbook.databinding.FragmentSettingsBinding
import com.example.songbook.ui.MainActivity
import com.example.songbook.ui.settings.dialogs.AboutDialogFragment
import com.example.songbook.ui.settings.dialogs.FontSizeDialogFragment
import com.example.songbook.ui.settings.dialogs.LanguageDialogFragment
import com.example.songbook.ui.settings.dialogs.ThemeDialogFragment
import com.example.songbook.util.LanguageHelper


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupLanguageDialogFragmentListener()
        setupThemeDialogFragmentListener()
        setupFontDialogFragmentListener()
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

            buttonFontSize.setOnClickListener {
                showFontDialogFragment()
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
            val languageKey = result.getInt(LanguageDialogFragment.KEY_LANGUAGE_RESPONSE)
            if (languageKey == 0) {
                val currentLanguage = LanguageHelper.getLanguage(requireContext())
                val desiredLanguage = "en"
                if (currentLanguage != desiredLanguage) {
                    LanguageHelper.changeLanguage(requireContext(), desiredLanguage)
                    restartMainActivity()

                }
            } else if (languageKey == 1) {
                val currentLanguage = LanguageHelper.getLanguage(requireContext())
                val desiredLanguage = "uk"
                if (currentLanguage != desiredLanguage) {
                    LanguageHelper.changeLanguage(requireContext(), desiredLanguage)
                    restartMainActivity()
                }
            }
        }
    }

    private fun restartMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        activity?.finish()
        activity?.startActivity(intent)
    }

    private fun showThemeDialogFragment() {
        ThemeDialogFragment.show(childFragmentManager)
    }

    private fun setupThemeDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            ThemeDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val which = result.getInt(ThemeDialogFragment.KEY_THEME_RESPONSE)
            when (which) {
                0 -> {
                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                }
                1 -> {
                    setDefaultNightMode(MODE_NIGHT_NO)
                }
                2 -> {
                    setDefaultNightMode(MODE_NIGHT_YES)
                }
            }
        }
    }

    private fun showFontDialogFragment() {
        FontSizeDialogFragment.show(childFragmentManager)
    }

    private fun setupFontDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            FontSizeDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val fontSize = result.getInt(FontSizeDialogFragment.KEY_FONT_SIZE_RESPONSE)
            when (fontSize) {
                0 -> {
                    activity?.setTheme(R.style.FontSizeSmall)
                }
                1 -> {
                    activity?.setTheme(R.style.FontSizeMedium)
                }
                2 -> {
                    activity?.setTheme(R.style.FontSizeLarge)
                }
            }
        }
    }

    private fun showAboutDialogFragment() {
        AboutDialogFragment.show(childFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}