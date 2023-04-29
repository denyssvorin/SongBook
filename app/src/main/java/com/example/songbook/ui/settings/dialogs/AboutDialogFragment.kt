package com.example.songbook.ui.settings.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.songbook.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AboutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.message))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .create()

    companion object {
        @JvmStatic
        val TAG: String = AboutDialogFragment::class.java.simpleName

        fun show(manager: FragmentManager) {
            val dialogFragment = AboutDialogFragment()
            dialogFragment.show(manager, TAG)
        }
    }
}