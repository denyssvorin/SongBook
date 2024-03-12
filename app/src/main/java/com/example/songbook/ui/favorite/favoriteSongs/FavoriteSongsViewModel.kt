package com.example.songbook.ui.favorite.favoriteSongs

import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteSongsViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    private var receivedBandName: String = "band"

    val searchQuery = MutableStateFlow("")

    // flow that receives songs by receivedBandName and search it via searchView
    private val _favSongsFlow = searchQuery.flatMapLatest { query ->
        songDao.getSongByBand(query, receivedBandName!!).map { songList ->
            songList.filter { song ->
                song.isFavorite
            }.toSet().toList()
        }
    }

    val favSongs = _favSongsFlow.asLiveData()

    private val songsEventChannel = Channel<SongsEvent>()
    val songsEvent = songsEventChannel.receiveAsFlow()

    fun onBandLoaded(favBand: String) {
        receivedBandName = favBand
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        songsEventChannel.send(SongsEvent.NavigateToSingleSongScreen(song))
    }

    fun addToFavorite(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }

    sealed class SongsEvent {
        data class NavigateToSingleSongScreen(val song: Song) : SongsEvent()
    }
}