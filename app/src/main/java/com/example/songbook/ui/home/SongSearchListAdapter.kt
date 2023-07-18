package com.example.songbook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Song
import com.example.songbook.databinding.SongSearchedItemBinding
import com.example.songbook.ui.contract.OnSongClickListener
import com.example.songbook.ui.home.songs.SongsListAdapter

class SongSearchListAdapter(private val listener: OnSongClickListener)
    : ListAdapter<Song, SongSearchListAdapter.SongSearchViewHolder>(SongsListAdapter.DiffSongsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val binding = SongSearchedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class SongSearchViewHolder(private val binding: SongSearchedItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val song = getItem(position)
                        listener.onSongClick(song)
                    }
                }
                iconAddToFavoriteSearched.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val song = getItem(position)
                        listener.addToFavorite(song, iconAddToFavoriteSearched.isChecked)
                    }
                }
            }
        }
        fun bind(song: Song) {
            binding.apply {
                textViewSongNameSearched.text = song.songName
                iconAddToFavoriteSearched.isChecked = song.isFavorite
                textViewBandNameSearched.text = song.bandName
            }
        }
    }
}