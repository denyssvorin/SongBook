package com.example.songbook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.data.Band
import com.example.songbook.databinding.ListItemBinding

class UserHomeBandsListAdapter (private val listener: OnItemClickListener)
    : ListAdapter<Band, UserHomeBandsListAdapter.UserBandsViewHolder>(DiffBandsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBandsViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return UserBandsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserBandsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class UserBandsViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

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

        fun bind(band: Band) {
            binding.textViewListItem.text = band.bandName
        }
    }

    interface OnItemClickListener {
        fun onItemClick(band: Band)
    }

    class DiffBandsCallback : DiffUtil.ItemCallback<Band>() {
        override fun areItemsTheSame(oldItem: Band, newItem: Band) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Band, newItem: Band) =
            oldItem == newItem
    }

}