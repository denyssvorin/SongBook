package com.example.songbook.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.songbook.R
import com.example.songbook.contract.*

class ItemFragment : Fragment(), HasCustomTitle {

    private lateinit var title : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val args = this.arguments
        val selectedTitle = args?.getString(KEY_ITEMS)
        title = selectedTitle.toString()

        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getTitleRes(): String = title

    companion object {
        @JvmStatic private val KEY_ITEMS = "KEY_ITEMS"

        @JvmStatic
        fun newInstance(song_name: String) : ItemFragment {
            val args = Bundle()
            args.putString(KEY_ITEMS, song_name)
            val fragment = ItemFragment()
            fragment.arguments = args
            return fragment
        }
    }
}