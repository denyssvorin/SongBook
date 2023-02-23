package com.example.songbook.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.*
import com.example.songbook.contract.CustomAction
import com.example.songbook.contract.HasCustomActions
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentSongsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment(), UserSongsListAdapter.OnItemClickListener, HasCustomTitle, HasCustomActions {

    private var _binding: FragmentSongsBinding? = null
    lateinit var title : String

    private val viewModel : SongsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)

        val args = this.arguments
        val selectedTitle = args?.getString(KEY_SONGS)
        title = selectedTitle.toString()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songFragment = UserSongsListAdapter(this)

        binding.recycleViewSongs.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = songFragment
        }

        viewModel.songs.observe(viewLifecycleOwner) {
            songFragment.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.songsEvent.collect() { event ->
                 when (event) {
                     is SongsViewModel.SongsEvent.NavigateToSingleSongScreen -> {
                         val action = SongsFragmentDirections.actionSongsFragmentToSingleSongFragment(event.song)
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

    override fun getTitleRes(): String = title

    override fun getCustomAction(): CustomAction {
            return CustomAction(
                iconRes = R.drawable.ic_search_24,
                textRes = R.string.search,
                onCustomAction = Runnable {
                    // action
                }
            )
    }

    companion object {
        @JvmStatic private val KEY_SONGS = "KEY_SONGS"

        @JvmStatic
        fun newInstance(band_name: String) : SongsFragment {
            val args = Bundle()
            args.putString(KEY_SONGS, band_name)
            val fragment = SongsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onItemClick(song: Song) {
        viewModel.onSongSelected(song)
    }

}