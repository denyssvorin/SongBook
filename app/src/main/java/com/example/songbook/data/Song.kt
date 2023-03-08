package com.example.songbook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random
import kotlin.random.nextInt

@Entity(tableName = "song_table")

@Parcelize
data class Song (
    @PrimaryKey(autoGenerate = false) val songName: String,
    val bandName: String,
    val isFavorite: Boolean = false,
    //val id: Int = Random.nextInt(1..3)
    ): Parcelable