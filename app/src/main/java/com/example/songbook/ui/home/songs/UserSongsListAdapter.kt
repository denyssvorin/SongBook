package com.example.songbook.ui.home.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Song
import com.example.songbook.databinding.SongItemBinding

class UserSongsListAdapter(private val listener: OnItemClickListener)
    : ListAdapter<Song, UserSongsListAdapter.UserSongsViewHolder>(DiffSongsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSongsViewHolder {
        val binding = SongItemBinding.inflate( LayoutInflater.from(parent.context), parent,false)
        return UserSongsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserSongsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class UserSongsViewHolder(private val binding: SongItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val song = getItem(position)
                        listener.onItemClick(song)
                    }
                }
                iconAddToFavorite.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val song = getItem(position)
                        listener.addToFavorite(song, iconAddToFavorite.isChecked)
                    }
                }
            }
        }
        fun bind(song: Song) {
            binding.apply {
                textViewSongItem.text = song.songName
                iconAddToFavorite.isChecked = song.isFavorite
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(song: Song)

        fun addToFavorite(song: Song, isFavorite: Boolean)
    }

    class DiffSongsCallback: DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.songName == newItem.songName

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem == newItem
    }
}