package com.example.songbook.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import kotlin.properties.Delegates.notNull


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
            val which = result.getInt(LanguageDialogFragment.KEY_LANGUAGE_RESPONSE)
            if (which == 0) {
                val currentLanguage = LanguageHelper.getLanguage(requireContext())
                val desiredLanguage = "en"
                if (currentLanguage != desiredLanguage) {
                    LanguageHelper.changeLanguage(requireContext(), desiredLanguage)
                    // перезапускає MainActivity для зміни тексту в усій програми
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            } else if (which == 1) {
                val currentLanguage = LanguageHelper.getLanguage(requireContext())
                val desiredLanguage = "uk"
                if (currentLanguage != desiredLanguage) {
                    LanguageHelper.changeLanguage(requireContext(), desiredLanguage)
                    // перезапускає MainActivity для зміни тексту в усій програми
                    val intent = Intent(activity, MainActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                }
            }
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