package com.example.songbook.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random
import kotlin.random.nextInt

@Entity(tableName = "band_table")

@Parcelize
data class Band (
    @PrimaryKey(autoGenerate = false) val bandName: String,
) : Parcelable
