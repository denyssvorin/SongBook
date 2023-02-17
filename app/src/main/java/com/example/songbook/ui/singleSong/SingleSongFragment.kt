package com.example.songbook.ui.singleSong

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.songbook.R
import com.example.songbook.contract.*

class SingleSongFragment : Fragment(), HasCustomTitle {

    private lateinit var title : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val args = this.arguments
        val selectedTitle = args?.getString(KEY_ITEMS)
        title = selectedTitle.toString()

        return inflater.inflate(R.layout.fragment_single_song, container, false)
    }

    override fun getTitleRes(): String = title

    companion object {
        @JvmStatic private val KEY_ITEMS = "KEY_ITEMS"

        @JvmStatic
        fun newInstance(song_name: String) : SingleSongFragment {
            val args = Bundle()
            args.putString(KEY_ITEMS, song_name)
            val fragment = SingleSongFragment()
            fragment.arguments = args
            return fragment
        }
    }
}