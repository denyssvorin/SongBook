package com.example.songbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Band
import com.example.songbook.R
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), HasCustomTitle {


    val bandsList = mutableListOf(
        Band(0, "Band"),
        Band(1,"Band1"),
        Band(2,"Band2"),
        Band(3,"Band3"),
        Band(4,"Band4"),
        Band(5,"Band5"),
        Band(6,"Band6"),
        Band(7,"Band7"),
        Band(8,"Band8"),
        Band(9,"Band9"),
        )

    private var _binding: FragmentHomeBinding? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val myAdapter = UserHomeBandsListAdapter(bandsList)
        binding.recycleViewBands.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = myAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTitleRes(): String = getString(R.string.title_home)

}