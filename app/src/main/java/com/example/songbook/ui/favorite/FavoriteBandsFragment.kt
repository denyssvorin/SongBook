package com.example.songbook.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.example.songbook.databinding.FragmentFavoriteBinding
import com.example.songbook.ui.contract.OnBandClickListener
import com.example.songbook.ui.home.HomeBandsListAdapter
import com.example.songbook.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteBandsFragment : Fragment(), OnBandClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val viewModel: FavoriteBandsViewModel by viewModels()
    private lateinit var searchView: SearchView

    // listener to scroll to start of list in searchView
    private val preDrawListListener = ViewTreeObserver.OnPreDrawListener {
        if (this.isAdded && _binding != null) {
            binding.recyclerViewFavBand.scrollToPosition(0)
        }
        true
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favBandsAdapter = HomeBandsListAdapter(this)
        binding.recyclerViewFavBand.apply {
            adapter = favBandsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            viewTreeObserver.addOnPreDrawListener(preDrawListListener)
        }

        viewModel.favBands.observe(viewLifecycleOwner) {
            favBandsAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.favBandsEvent.collect() { event ->
                when (event) {
                    is FavoriteBandsViewModel.FavEvent.NavigateToFavSongsScreen -> {
                       val action = FavoriteBandsFragmentDirections
                           .actionNavigationFavoriteToFavoriteSongsFragment(event.favBand)
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
                viewModel.searchQueryLiveData.observe(viewLifecycleOwner) { searchText ->
                    if (searchText.isBlank() && searchView.isShown) {
                        Log.i("TAG", "FavoriteBandsFragment onCreateMenu: searchText is blank")

                        preDrawListListener.onPreDraw()
                    }
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

    override fun onBandClick(bandWithSongs: String) {
        viewModel.onBandSelected(bandWithSongs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewFavBand.viewTreeObserver.removeOnPreDrawListener(preDrawListListener)
        searchView.setOnQueryTextListener(null)
        _binding = null
    }
}