package com.example.songbook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "song_table")

@Parcelize
data class Song (
    @PrimaryKey(autoGenerate = false) val songName: String,
    val bandName: String,
    val isFavorite: Boolean = false,
    ): Parcelable