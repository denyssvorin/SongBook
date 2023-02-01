package com.example.songbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.databinding.ItemSongBinding
import com.example.songbook.fragments.SongsFragment

class UserBandsListAdapter(
    var bandsList: List<BandData>
) : RecyclerView.Adapter<UserBandsListAdapter.UserBandsViewHolder>() {

    class UserBandsViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var bandName: TextView
        private val binding = ItemSongBinding.bind(item)

        init {
            bandName = binding.tvUserBand
        }
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
                .replace(R.id.nav_host_fragment_activity_main, SongsFragment.newInstance(newTitle))
                .commit()

        }

    }

    override fun getItemCount(): Int {
        return bandsList.size
    }

}