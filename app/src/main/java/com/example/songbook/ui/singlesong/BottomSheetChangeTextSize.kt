package com.example.songbook.ui.singlesong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.songbook.databinding.BottomSheetChangeTextSizeLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetChangeTextSize(val listener: BottomSheetListener) : BottomSheetDialogFragment() {

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

        val sharedPref = activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        var savedTextSizeValue = sharedPref?.getInt("single_song_text_size", 16) ?: 16
        binding.textSizeValue.text = savedTextSizeValue.toString()

        binding.increaseTextSize.setOnClickListener {
            savedTextSizeValue += 2
            binding.textSizeValue.text = savedTextSizeValue.toString()
            editor?.apply {
                putInt("single_song_text_size", savedTextSizeValue)
                apply()
            }
            listener.increaseText(savedTextSizeValue.toFloat())
        }

        binding.decreaseTextSize.setOnClickListener {
            savedTextSizeValue -= 2
            editor?.apply {
                putInt("single_song_text_size", savedTextSizeValue)
                apply()
            }
            binding.textSizeValue.text = savedTextSizeValue.toString()
            listener.decreaseText(savedTextSizeValue.toFloat())
        }
    }

    interface BottomSheetListener {
        fun increaseText(value: Float)
        fun decreaseText(value: Float)
    }

    companion object {
        @JvmStatic val KEY_TEXT_SIZE_RESULT = "KEY_TEXT_SIZE_RESULT"
        @JvmStatic val KEY_SIZE_RESPONSE = "KEY_SIZE_RESPONSE"
    }

}