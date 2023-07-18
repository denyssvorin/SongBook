package com.example.songbook.ui.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentHomeBinding
import com.example.songbook.ui.contract.OnBandClickListener
import com.example.songbook.ui.contract.OnSongClickListener
import com.example.songbook.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnBandClickListener, OnSongClickListener {

    private val viewModel : HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    private val bandAdapter : HomeBandsListAdapter by lazy { HomeBandsListAdapter(this) }
    private val songAdapter : SongSearchListAdapter by lazy { SongSearchListAdapter(this) }
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        binding.recyclerViewBand.apply {
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

                    is HomeViewModel.BandsEvent.NavigateToSingleSongScreen -> {
                        val action = HomeFragmentDirections.actionNavigationHomeToSingleSongFragment(event.song,
                            event.song.songName)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val searchIcon = menu.findItem(R.id.action_search)
                searchIcon.isVisible = true
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar, menu)

                searchViewItem = menu.findItem(R.id.action_search)
                searchView = searchViewItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchViewItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }
                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                    initSongList()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initSongList() {
        binding.recyclerViewSongSearched.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = songAdapter
        }
        viewModel.songs.observe(viewLifecycleOwner) { songList ->
            songAdapter.submitList(songList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView.setOnQueryTextListener(null)
    }

    override fun onBandClick(bandWithSongs: String) {
        viewModel.onBandSelected(bandWithSongs)
    }

    override fun onSongClick(song: Song) {
        viewModel.onSongSelected(song)
    }

    override fun addToFavorite(song: Song, isFavorite: Boolean) {
        viewModel.addToFavorite(song, isFavorite)
    }
}