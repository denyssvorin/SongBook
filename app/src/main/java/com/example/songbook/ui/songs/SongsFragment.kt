package com.example.songbook.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.songbook.*
import com.example.songbook.contract.CustomAction
import com.example.songbook.contract.HasCustomActions
import com.example.songbook.contract.HasCustomTitle
import com.example.songbook.contract.navigator
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentSongsBinding

class SongsFragment : Fragment(), HasCustomTitle, HasCustomActions {

    private var _binding: FragmentSongsBinding? = null
    lateinit var title : String
    val songsList = mutableListOf(
        Song(0,"Song"),
        Song(1,"Song1"),
        Song(2,"Song2"),
        Song(3,"Song3"),
        Song(4,"Song4"),
        Song(5,"Song5"),
        Song(6,"Song6"),
        Song(7,"Song7"),
        Song(8,"Song8"),
        Song(9,"Song9"),
        Song(10,"Song10"),
    )

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val args = this.arguments
        val selectedTitle = args?.getString(KEY_SONGS)
        title = selectedTitle.toString()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myAdapter = UserSongsListAdapter(songsList)
        binding.recycleViewSongs.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = myAdapter
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
                    onConfirmPressed()
                }
            )
    }


    private fun onConfirmPressed() {
        navigator().goBack()
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
}