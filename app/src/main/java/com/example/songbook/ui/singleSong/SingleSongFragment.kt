package com.example.songbook.ui.singleSong

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.example.songbook.R

class SingleSongFragment : Fragment() {

    private val args : SingleSongFragmentArgs by navArgs()
    private var isFavorite : Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setupMenu()
        return inflater.inflate(R.layout.fragment_single_song, container, false)
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
            }
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.action_add_to_favorite -> {
                        Toast.makeText(requireContext(),"Added", Toast.LENGTH_SHORT).show()
                        menuItem.setIcon(R.drawable.ic_favorite_checked)
                        return true
                    }
                }
             return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}