package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.songbook.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.properties.Delegates

class LanguageDialogFragment : DialogFragment() {

    private lateinit var languageList: Array<String>
    private var savedStateIndex by Delegates.notNull<Int>()

    // initializing in onAttach because these variables requires context
    override fun onAttach(context: Context) {
        super.onAttach(context)

        languageList = arrayOf(
            requireContext().getString(R.string.english),
            requireContext().getString(R.string.ukrainian)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val sharedPref =
            this.activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        var selectedItemIndex = sharedPref?.getInt("language_radio_button", 0) ?: 0

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.language))
            .setSingleChoiceItems(languageList, selectedItemIndex) { dialog, which ->
                selectedItemIndex = which
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                editor?.apply {
                    putInt("language_radio_button", selectedItemIndex)
                    apply()
                }
                val indexData = selectedItemIndex
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(KEY_LANGUAGE_RESPONSE to indexData)
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