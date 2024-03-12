package com.example.songbook.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.MenuItem.OnActionExpandListener
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentHomeBinding
import com.example.songbook.ui.contract.OnBandClickListener
import com.example.songbook.ui.contract.OnSongClickListener
import com.example.songbook.util.onQueryTextChanged
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(), OnBandClickListener, OnSongClickListener {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    private val bandAdapter: HomeBandsListAdapter by lazy { HomeBandsListAdapter(this) }
    private val songAdapter: SongSearchListAdapter by lazy { SongSearchListAdapter(this) }
    private val concatAdapter = ConcatAdapter(bandAdapter, songAdapter)
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem

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

    override fun onStart() {
        super.onStart()
        // to hide songs when user came back to the program
        observeAuthenticationState()
    }

    private fun collectBandList() {
        viewModel.retrieveNewLatestData()
        viewModel.bands.observe(viewLifecycleOwner) {
            bandAdapter.submitList(it)
        }
    }

    private fun collectEmptyListValue() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isCombinedListEmpty.collectLatest { isListEmpty ->
                if (isListEmpty) {
                    delay(2000)
                    binding.textViewIfListIsEmpty.visibility = View.VISIBLE
                } else {
                    binding.textViewIfListIsEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun collectNavigation() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bandsEvent.collect { event ->
                when (event) {
                    is HomeViewModel.BandsEvent.NavigateToSongsListScreen -> {
                        val action =
                            HomeFragmentDirections.actionNavigationHomeToSongsFragment(event.band)
                        findNavController().navigate(action)
                    }

                    is HomeViewModel.BandsEvent.NavigateToSingleSongScreen -> {
                        val action =
                            HomeFragmentDirections.actionNavigationHomeToSingleSongFragment(
                                event.song,
                                event.song.songName // to set name in toolbar
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun observeAuthenticationState() {
        val user = Firebase.auth.currentUser
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            if (user != null) {
                initUi()
            } else {
                findNavController().navigate(R.id.loginFragment)
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

                searchViewItem.setOnActionExpandListener(object : OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                        if (searchView.isNotEmpty()) {
                            viewModel.startCollect()
                            observeSongs()
                            binding.recyclerViewBand.adapter = concatAdapter

                            viewModel.searchQueryLiveData.observe(viewLifecycleOwner) { searchText ->

                                if (searchText.isBlank() && searchView.isShown && concatAdapter.itemCount > 0) {
                                    Log.i("TAG", "HomeFragment onCreateMenu: searchText is blank")

                                    scrollToTop()
                                }
                            }
                        } else {
                            initBandAdapter()
                        }
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        viewModel.stopCollect()
                        initBandAdapter()
                        return true
                    }

                })

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchViewItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)

                }

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeSongs() {
        viewModel.songsList.observe(viewLifecycleOwner) { songList ->
            songAdapter.submitList(songList)
        }
    }

    private fun initUi() {
        initBandAdapter()
        collectBandList()
        collectEmptyListValue()
        collectNavigation()
    }

    private fun initBandAdapter() {
        binding.recyclerViewBand.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bandAdapter
        }
    }

    private fun scrollToTop() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.recyclerViewBand.smoothScrollToPosition(0)
        }, 200)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TAG", "onDestroyView: ")
        if (::searchView.isInitialized && ::searchViewItem.isInitialized) {
            searchView.setOnQueryTextListener(null)
            searchViewItem.setOnActionExpandListener(null)
        }
        _binding = null
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