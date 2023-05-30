package com.example.songbook.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Song
import com.example.songbook.databinding.BandItemBinding

class FavoriteListAdapter(private val listener: OnItemClickListener):
    ListAdapter<String, FavoriteListAdapter.FavoriteBandsViewHolder>(DiffBandsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBandsViewHolder {
        val binding = BandItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return FavoriteBandsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteBandsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class FavoriteBandsViewHolder(private val binding: BandItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val band = getItem(position)
                        listener.onItemClick(band)
                    }
                }
            }
        }

        fun bind(favBand: String) {
            binding.textViewBandItem.text = favBand
        }
    }

    interface OnItemClickListener {
        fun onItemClick(bandWithSongs: String)
    }

    class DiffBandsCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem
    }

}