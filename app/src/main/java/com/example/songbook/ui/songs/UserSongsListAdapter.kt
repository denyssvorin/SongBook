package com.example.songbook.ui.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Song
import com.example.songbook.databinding.ListItemBinding

class UserSongsListAdapter(private val listener: OnItemClickListener)
    : ListAdapter<Song, UserSongsListAdapter.UserSongsViewHolder>(DiffSongsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSongsViewHolder {
        val binding = ListItemBinding.inflate( LayoutInflater.from(parent.context), parent,false)
        return UserSongsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserSongsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class UserSongsViewHolder(private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val song = getItem(position)
                        listener.onItemClick(song)
                    }
                }
            }
        }
        fun bind(song: Song) {
            binding.textViewListItem.text = song.songName
            // add_to_favorite logic
        }
    }

    interface OnItemClickListener {
        fun onItemClick(song: Song)

        // implement add_to_favorite logic
    }

    class DiffSongsCallback: DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem == newItem
    }
}