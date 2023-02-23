package com.example.songbook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "song_table")

@Parcelize
data class Song (
    val songName: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
    ): Parcelable