package com.example.songbook.ui.singlesong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.rangeTo
import com.example.songbook.databinding.BottomSheetChangeTextSizeLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetChangeTextSize(private val listener: BottomSheetListener) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangeTextSizeLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChangeTextSizeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref =
            activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        var savedTextSizeValue = sharedPref?.getInt("single_song_text_size", 16) ?: 16
        binding.textSizeValue.text = savedTextSizeValue.toString()

        binding.increaseTextSize.setOnClickListener {
            if (savedTextSizeValue in 4..63) {
                savedTextSizeValue += 2
                editor?.apply {
                    putInt("single_song_text_size", savedTextSizeValue)
                    apply()
                }
                binding.textSizeValue.text = savedTextSizeValue.toString()
                listener.increaseText(savedTextSizeValue.toFloat())
            }
        }

        binding.decreaseTextSize.setOnClickListener {
            if (savedTextSizeValue in 5 .. 64) {
                savedTextSizeValue -= 2
                editor?.apply {
                    putInt("single_song_text_size", savedTextSizeValue)
                    apply()
                }
                binding.textSizeValue.text = savedTextSizeValue.toString()
                listener.decreaseText(savedTextSizeValue.toFloat())
            }
        }
    }

    interface BottomSheetListener {
        fun increaseText(value: Float)
        fun decreaseText(value: Float)
    }

    companion object {
        @JvmStatic
        val KEY_SHOW_TEXT_SIZE = "KEY_SHOW_TEXT_SIZE"
    }

}