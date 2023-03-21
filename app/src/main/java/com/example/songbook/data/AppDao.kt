package com.example.songbook.data

import androidx.room.*
import com.example.songbook.data.relations.BandWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM band_table WHERE bandName LIKE '%' || :searchQuery || '%' ORDER BY bandName")
    fun getBands(searchQuery: String) : Flow<List<Band>>

    @Query("SELECT * FROM song_table WHERE songName LIKE '%' || :searchQuery || '%' ORDER BY songName")
    fun getSongs(searchQuery: String) : Flow<List<Song>>

    @Query("SELECT textSong FROM song_table WHERE songName LIKE '%' || :searchQuery || '%'")
    fun getTextSong(searchQuery: String): Flow<String>

    @Transaction
    @Query("SELECT * FROM band_table WHERE bandName LIKE '%' || :searchQuery || '%' ORDER BY bandName")
    fun getBandWithSongs(searchQuery: String) : Flow<List<BandWithSongs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBand(band: Band)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Update
    suspend fun update(band: Band)

    @Update
    suspend fun update(song: Song)

    @Delete
    suspend fun delete(band: Band)

    @Delete
    suspend fun delete(song: Song)
}