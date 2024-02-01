package com.example.songbook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.songbook.databinding.BandItemBinding
import com.example.songbook.ui.contract.OnBandClickListener

class HomeBandsListAdapter (private val listener: OnBandClickListener)
    : ListAdapter<String, HomeBandsListAdapter.UserBandsViewHolder>(DiffBandsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBandsViewHolder {
        val binding = BandItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return UserBandsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserBandsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class UserBandsViewHolder(private val binding: BandItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val band = getItem(position)
                        listener.onBandClick(band)
                    }
                }
            }
        }

        fun bind(band: String) {
            binding.textViewBandItem.text = band
        }
    }

    class DiffBandsCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem
    }
}