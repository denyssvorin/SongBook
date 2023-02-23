package com.example.songbook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "band_table")

@Parcelize
data class Band (
    val bandName: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
