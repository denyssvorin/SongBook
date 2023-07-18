package com.example.songbook.ui.contract

import com.example.songbook.data.Song

interface OnSongClickListener {
    fun onSongClick(song: Song)

    fun addToFavorite(song: Song, isFavorite: Boolean)
}