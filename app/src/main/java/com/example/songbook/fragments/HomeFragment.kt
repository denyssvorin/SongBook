package com.example.songbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.BandData
import com.example.songbook.R
import com.example.songbook.UserBandsListAdapter
import com.example.songbook.contract.CustomAction
import com.example.songbook.contract.HasCustomActions
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.contract.navigator
import com.example.songbook.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), HasCustomTitle, HasCustomActions {


    val bandsList = mutableListOf(
        BandData(0, "Band"),
        BandData(1,"Band1"),
        BandData(2,"Band2"),
        BandData(3,"Band3"),
        BandData(4,"Band4"),
        BandData(5,"Band5"),
        BandData(6,"Band6"),
        BandData(7,"Band7"),
        BandData(8,"Band8"),
        BandData(9,"Band9"),
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
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val myAdapter = UserBandsListAdapter(bandsList)
        binding.recycleViewBands.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = myAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.ic_search_24,
            textRes = R.string.search,
            onCustomAction = Runnable {
                onConfirmPressed()
            }
        )
    }

    private fun onConfirmPressed() {
        navigator().goBack()
    }

    override fun getTitleRes(): String = getString(R.string.title_home)

}