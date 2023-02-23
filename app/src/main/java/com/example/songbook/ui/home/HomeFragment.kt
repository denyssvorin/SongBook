package com.example.songbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.data.Band
import com.example.songbook.R
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.databinding.FragmentHomeBinding
import com.example.songbook.ui.songs.SongsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(), UserHomeBandsListAdapter.OnItemClickListener, HasCustomTitle {

    private val viewModel : HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

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

        val bandAdapter = UserHomeBandsListAdapter(this)

        binding.recycleViewBands.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bandAdapter
        }

        viewModel.bands.observe(viewLifecycleOwner) {
            bandAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bandsEvent.collect() { event ->
                when (event) {
                    is HomeViewModel.BandsEvent.NavigateToSongsListScreen -> {
                        val action = HomeFragmentDirections.actionNavigationHomeToSongsFragment(event.band)
                        findNavController().navigate(action)

                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTitleRes(): String = getString(R.string.title_home)

    override fun onItemClick(band: Band) {
        viewModel.onBandSelected(band)
    }

}