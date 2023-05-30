package com.example.songbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM song_table WHERE bandName LIKE '%' || :searchQuery || '%'")
    fun getSongs(searchQuery: String): Flow<List<Song>>

    @Query("SELECT textSong FROM song_table WHERE songName LIKE '%' || :searchQuery || '%'")
    fun getSongText(searchQuery: String): Flow<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Update
    suspend fun update(song: Song)

    @Delete
    suspend fun delete(song: Song)
}