package com.example.songbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM song_table WHERE bandName LIKE '%' || :searchQuery || '%' ORDER BY bandName")
    fun getBands(searchQuery: String): Flow<List<Song>>

    @Query("SELECT * FROM song_table WHERE songName LIKE '%' || :searchQuery || '%' ORDER BY songName")
    fun getSongs(searchQuery: String): Flow<List<Song>>

    @Query("SELECT * FROM song_table WHERE songName LIKE '%' || :searchQuery || '%' AND bandName LIKE '%' || :searchBandName || '%' ORDER BY songName")
    fun getSongByBand(searchQuery: String, searchBandName: String): Flow<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Query("SELECT * FROM song_table WHERE songName LIKE '%' || :searchQuery || '%'")
    suspend fun getSongByName(searchQuery: String): Song

    @Update
    suspend fun update(song: Song)

    @Delete
    suspend fun delete(song: Song)
}