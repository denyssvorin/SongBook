package com.example.songbook.ui.favorite

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isNotEmpty
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavoriteBandsFragment : Fragment(), OnBandClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val viewModel: FavoriteBandsViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem
    private val favBandAdapter: HomeBandsListAdapter by lazy { HomeBandsListAdapter(this) }

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

        binding.recyclerViewFavBand.apply {
            adapter = favBandAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.favBands.observe(viewLifecycleOwner) {
            Log.i("FavFragment", "onViewCreated: favBandList = $it")
            favBandAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.favBandsEvent.collect { event ->
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

                searchViewItem = menu.findItem(R.id.action_search)
                searchView = searchViewItem.actionView as SearchView

                collectEmptyListValue()

                searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                        if (searchView.isNotEmpty()) {
                            viewModel.searchQueryLiveData.observe(viewLifecycleOwner) { searchText ->

                                if (searchText.isBlank() && searchView.isShown && favBandAdapter.itemCount > 0) {
                                    Log.i("TAG", "HomeFragment onCreateMenu: searchText is blank")

                                    scrollToTop()
                                }
                            }
                        }
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
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

    private fun scrollToTop() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.recyclerViewFavBand.smoothScrollToPosition(0)
        }, 200)
    }

    private fun collectEmptyListValue() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isFavListEmpty.collectLatest { isListEmpty ->
                if (isListEmpty && searchView.isShown && binding.textViewFavListIsEmpty.visibility == View.GONE) {
                    delay(2000)
                    binding.textViewIfListIsEmpty.visibility = View.VISIBLE
                } else if (searchView.isShown) {
                    binding.textViewIfListIsEmpty.visibility = View.GONE
                } else if (isListEmpty) {
                    delay(1000)
                    binding.textViewFavListIsEmpty.visibility = View.VISIBLE
                } else {
                    binding.textViewFavListIsEmpty.visibility = View.GONE
                }
            }
        }
    }

    override fun onBandClick(bandWithSongs: String) {
        viewModel.onBandSelected(bandWithSongs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::searchView.isInitialized) {
            searchView.setOnQueryTextListener(null)
            searchViewItem.setOnActionExpandListener(null)
        }
        _binding = null
    }
}