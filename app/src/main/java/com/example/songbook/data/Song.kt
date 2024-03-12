package com.example.songbook.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song (
    val bandName: String = "Band",
    val songName: String = "Song",
    val isFavorite: Boolean = false,
    val textSong: String = "TEXT OF SONG"
    ): Parcelable