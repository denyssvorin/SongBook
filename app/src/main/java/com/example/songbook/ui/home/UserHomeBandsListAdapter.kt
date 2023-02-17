package com.example.songbook.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.R
import com.example.songbook.data.Band
import com.example.songbook.databinding.ItemSongBinding
import com.example.songbook.ui.songs.SongsFragment

class UserHomeBandsListAdapter(
    var bandsList: List<Band>
) : RecyclerView.Adapter<UserHomeBandsListAdapter.UserBandsViewHolder>() {

    class UserBandsViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ItemSongBinding.bind(item)
        var bandName = binding.tvUserBand
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBandsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent,false)
        return UserBandsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserBandsViewHolder, position: Int) {
        holder.bandName.text = bandsList[position].bandName

        holder.bandName.setOnClickListener { p0 ->
            val newTitle =  holder.bandName.text.toString()
            val activity = p0!!.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment, SongsFragment.newInstance(newTitle))
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return bandsList.size
    }

}