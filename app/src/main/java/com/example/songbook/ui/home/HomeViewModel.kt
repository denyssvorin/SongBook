package com.example.songbook.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val bandsFlow = searchQuery.flatMapLatest { query ->
        songDao.getBands(query).map { songList ->
            songList.map {
                it.bandName
            }.toSet().toList()
        }
    }

    val bands = bandsFlow.map {
        return@map it
    }.asLiveData()



    private val songsFlow = searchQuery.flatMapLatest { query ->
        songDao.getSongs(query).map { songList ->
            songList.toSet().toList()
        }
    }

    val songs = songsFlow.asLiveData()



    private val bandsEventChannel = Channel<BandsEvent>()
    val bandsEvent = bandsEventChannel.receiveAsFlow()

    fun onBandSelected(bandWithSongs: String) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSongsListScreen(bandWithSongs))
    }
    fun onSongSelected(song: Song) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSingleSongScreen(song))
    }
    fun addToFavorite(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }

    sealed class BandsEvent {
        data class NavigateToSongsListScreen(val band: String) : BandsEvent()
        data class NavigateToSingleSongScreen(val song: Song) : BandsEvent()
    }
}