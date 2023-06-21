package com.example.songbook.ui.favorite.favoriteSongs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentFavoriteSongsBinding
import com.example.songbook.ui.home.songs.UserSongsListAdapter
import com.example.songbook.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteSongsFragment : Fragment(), UserSongsListAdapter.OnItemClickListener{

    private var _binding: FragmentFavoriteSongsBinding? = null
    private lateinit var searchView: SearchView
    private val args: FavoriteSongsFragmentArgs by navArgs()

    private val viewModel : FavoriteSongsViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteSongsBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songAdapter = UserSongsListAdapter(this)

        binding.recycleViewFavoriteSongs.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = songAdapter
        }

        viewModel.songs.observe(viewLifecycleOwner) { songList ->
            songAdapter.submitList(songList)
        }

        viewModel.onBandLoaded(args.bandName)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.songsEvent.collect() { event ->
                when (event) {
                    is FavoriteSongsViewModel.SongsEvent.NavigateToSingleSongScreen -> {
                        val action = FavoriteSongsFragmentDirections.actionFavoriteSongsFragmentToSingleSongFragment(
                            event.song, event.song.songName)
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

                val search = menu.findItem(R.id.action_search)
                searchView = search.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    search.expandActionView()
                    searchView.setQuery(pendingQuery,false)
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

    override fun onItemClick(song: Song) {
        viewModel.onSongSelected(song)
    }

    override fun addToFavorite(song: Song, isFavorite: Boolean) {
        viewModel.addToFavorite(song, isFavorite)
    }
}