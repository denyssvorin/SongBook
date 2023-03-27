package com.example.songbook.data

import androidx.room.*
import com.example.songbook.data.relations.BandWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface BandDao {

    @Transaction
    @Query("SELECT * FROM band_table WHERE bandName LIKE '%' || :searchQuery || '%' ORDER BY bandName")
    fun getBandWithSongs(searchQuery: String) : Flow<List<BandWithSongs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBand(band: Band)

    @Update
    suspend fun update(band: Band)

    @Delete
    suspend fun delete(band: Band)
}