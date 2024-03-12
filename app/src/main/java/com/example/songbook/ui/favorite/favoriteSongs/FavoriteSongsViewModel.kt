package com.example.songbook.ui.favorite.favoriteSongs

import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.repo.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteSongsViewModel @Inject constructor(
    private val songsRepository: SongsRepository
) : ViewModel() {

    private var receivedBandName: String = "band"

    val searchQuery = MutableStateFlow("")

    private val _favSongsFlow = searchQuery.flatMapLatest { query ->
        songsRepository.getFavoriteSongsOfTheBand(query, receivedBandName)
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
        songsRepository.addToFavorite(song, isFavorite)
    }

    sealed class SongsEvent {
        data class NavigateToSingleSongScreen(val song: Song) : SongsEvent()
    }
}