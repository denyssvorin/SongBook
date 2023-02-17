package com.example.songbook.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.ItemSongBinding
import com.example.songbook.ui.singleSong.SingleSongFragment

class UserSongsListAdapter(
    var songsList: List<Song>
) : RecyclerView.Adapter<UserSongsListAdapter.UserSongsViewHolder>() {

    class UserSongsViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ItemSongBinding.bind(item)
        var songName = binding.tvUserBand
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSongsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song,parent,false)
        return UserSongsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserSongsViewHolder, position: Int) {
        holder.songName.text = songsList[position].songName

        holder.songName.setOnClickListener { p0 ->
            val newTitle =  holder.songName.text.toString()
            val activity = p0!!.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.nav_host_fragment, SingleSongFragment.newInstance(newTitle))
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }
}