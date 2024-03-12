package com.example.songbook.ui.singlesong

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.songbook.databinding.BottomSheetChangeTextSizeLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetChangeTextSize(private val listener: BottomSheetListener) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangeTextSizeLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SingleSongViewModel by viewModels()

    private var savedTextSizeValue = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChangeTextSizeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.singleSongTextSizePreferencesFlow.collect { singleSongTextSizePreferences ->
                savedTextSizeValue = singleSongTextSizePreferences.textSize
                binding.textSizeValue.text = savedTextSizeValue.toString()
            }
        }

        binding.increaseTextSize.setOnClickListener {
            if (savedTextSizeValue in 4..63) {
                savedTextSizeValue += 2
                binding.textSizeValue.text = savedTextSizeValue.toString()
                listener.changeTextSize(savedTextSizeValue.toFloat())
            }
        }

        binding.decreaseTextSize.setOnClickListener {
            if (savedTextSizeValue in 5..64) {
                savedTextSizeValue -= 2
                binding.textSizeValue.text = savedTextSizeValue.toString()
                listener.changeTextSize(savedTextSizeValue.toFloat())
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.saveTextSize(savedTextSizeValue)
    }

    interface BottomSheetListener {
        fun changeTextSize(value: Float)
    }

    companion object {
        @JvmStatic
        val KEY_SHOW_TEXT_SIZE = "KEY_SHOW_TEXT_SIZE"
    }

}