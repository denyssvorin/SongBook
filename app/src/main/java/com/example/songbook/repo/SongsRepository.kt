package com.example.songbook.repo

import com.example.songbook.data.Song
import kotlinx.coroutines.flow.Flow

interface SongsRepository {

    suspend fun createPersonalFirestoreDocument(uid: String)


    fun getBandList(searchQuery: String): Flow<List<String>>

    fun getSongList(searchQuerySongName: String): Flow<List<Song>>


    fun getSongsOfTheBand(query: String, bandName: String): Flow<List<Song>>

    fun getSingleSong(bandName: String, songName: String): Flow<Song>


    fun getFavoriteValue(bandName: String, songName: String): Flow<Boolean>

    fun addToFavorite(song: Song, isFavorite: Boolean)


    fun getFavoriteBandList(searchQuery: String): Flow<List<String>>

    fun getFavoriteSongsOfTheBand(searchQuery: String, bandName: String): Flow<List<Song>>
}