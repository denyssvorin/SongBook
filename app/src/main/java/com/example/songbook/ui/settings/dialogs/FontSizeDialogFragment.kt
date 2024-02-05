package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.songbook.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FontSizeDialogFragment() : DialogFragment() {

    private lateinit var fontSizeList: Array<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fontSizeList = arrayOf(
            requireContext().getString(R.string.small),
            requireContext().getString(R.string.medium),
            requireContext().getString(R.string.large)
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val sharedPref =
            this.activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        var selectedItemIndex = sharedPref?.getInt("app_font_size", 1) ?: 1

        var selectedFontSize = fontSizeList[selectedItemIndex]

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.change_font_size))
            .setSingleChoiceItems(fontSizeList, selectedItemIndex) { dialog, which ->
                selectedItemIndex = which
                selectedFontSize = fontSizeList[which]
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                editor?.apply {
                    putString("app_font_size", selectedFontSize)
                    apply()
                }
                val indexData = selectedItemIndex
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(
                        KEY_FONT_SIZE_RESPONSE to indexData
                    )
                )
            }
            .create()
    }

    companion object {
        @JvmStatic
        val TAG: String = FontSizeDialogFragment::class.java.simpleName

        @JvmStatic
        val KEY_FONT_SIZE_RESPONSE: String = "KEY_FONT_SIZE_RESPONSE"

        @JvmStatic
        val REQUEST_KEY: String = "$TAG:defaultRequestKey"

        fun show(manager: FragmentManager) {
            val dialogFragment = FontSizeDialogFragment()
            dialogFragment.show(manager, TAG)
        }
    }
}