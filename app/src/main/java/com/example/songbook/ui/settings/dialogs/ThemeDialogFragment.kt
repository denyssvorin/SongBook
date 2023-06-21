package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.songbook.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class ThemeDialogFragment : DialogFragment() {

    private lateinit var themeList: Array<String>

    // initializing in onAttach because these variables requires context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        themeList = arrayOf(
            getString(R.string.use_system_theme),
            requireContext().getString(R.string.light),
            requireContext().getString(R.string.dark)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val sharedPref =
            this.activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        var selectedItemIndex = sharedPref?.getInt("theme_radio_button", 0) ?: 0

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.theme))
            .setSingleChoiceItems(themeList, selectedItemIndex) { _, which ->
                selectedItemIndex = which

                editor?.apply {
                    putInt("theme_radio_button", selectedItemIndex)
                    apply()
                }

                val indexData = selectedItemIndex
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(KEY_THEME_RESPONSE to indexData)
                )
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                editor?.apply {
                    putInt("theme_radio_button", selectedItemIndex)
                    apply()
                }
            }
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