package com.example.songbook.ui.singleSong

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentSingleSongBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleSongFragment : Fragment(), SingleSongViewModel.OnAddToFavoriteClickListener {

    private val args : SingleSongFragmentArgs by navArgs()
    private val viewModel : SingleSongViewModel by viewModels()

    private var _binding : FragmentSingleSongBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleSongBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.textSong.observe(viewLifecycleOwner) { text ->
            binding.textViewTextSong.text = text
        }

        viewModel.getSongBySongName(args.songName)
        viewModel.isFavorite = args.song.isFavorite

    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val searchIcon = menu.findItem(R.id.action_search)
                searchIcon.isVisible = false

                val addToFavoriteIcon = menu.findItem(R.id.action_add_to_favorite)

                if (args.song.isFavorite) {
                    addToFavoriteIcon.setIcon(R.drawable.ic_favorite_checked)
                }

                viewModel.resultSuccessFavorite.observe(viewLifecycleOwner) {
                    addToFavoriteIcon.setIcon(R.drawable.ic_favorite_checked)
                }
                viewModel.resultDeleteFavorite.observe(viewLifecycleOwner) {
                    addToFavoriteIcon.setIcon(R.drawable.ic_favorite_border)
                }
            }
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.action_add_to_favorite -> {
                        Toast.makeText(requireContext(),"Added", Toast.LENGTH_SHORT).show()
                        viewModel.setFavorite(args.song)

                        return true
                    }
                }
             return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun addToFavorite(song: Song, isFavorite: Boolean) {
        viewModel.addToFavoriteSong(song, isFavorite)
    }


}