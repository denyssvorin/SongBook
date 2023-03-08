package com.example.songbook.data.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.songbook.data.Band
import com.example.songbook.data.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BandWithSongs (
    @Embedded val band: Band,
    @Relation (
        parentColumn = "bandName",
        entityColumn = "bandName"
            )
    val songs : List<Song>
) : Parcelable
