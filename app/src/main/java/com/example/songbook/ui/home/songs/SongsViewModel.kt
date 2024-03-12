package com.example.songbook.ui.home.songs

import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.repo.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository
) : ViewModel() {

    private var receivedBandName: String? = "band"

    val searchQuery = MutableStateFlow("")

    private val songsFlow = searchQuery.flatMapLatest { query ->
        songsRepository.getSongsOfTheBand(query, receivedBandName!!).map { songList ->
            songList.toSet().toList()
        }
    }

    val songs = songsFlow.asLiveData()

    private val songsEventChannel = Channel<SongsEvent>()
    val songsEvent = songsEventChannel.receiveAsFlow()


    fun onBandLoaded(band: String) {
        receivedBandName = band
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        songsEventChannel.send(SongsEvent.NavigateToSingleSongScreen(song))
    }

    fun addToFavorite(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songsRepository.addToFavorite(song, isFavorite)
    }

    sealed class SongsEvent {
        data class NavigateToSingleSongScreen(val song: Song) : SongsEvent()
    }
}