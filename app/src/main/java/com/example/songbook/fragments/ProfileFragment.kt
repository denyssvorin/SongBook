package com.example.songbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.songbook.R
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), HasCustomTitle {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.imgProfile.setImageResource(R.drawable.ic_profile_circle)

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTitleRes(): String = getString(R.string.title_profile)
}