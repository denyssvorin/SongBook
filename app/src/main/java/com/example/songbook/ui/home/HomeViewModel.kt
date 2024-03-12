package com.example.songbook.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.repo.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songsRepository: SongsRepository
) : ViewModel() {

    private val bandsEventChannel = Channel<BandsEvent>()
    val bandsEvent = bandsEventChannel.receiveAsFlow()

    fun onBandSelected(bandWithSongs: String) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSongsListScreen(bandWithSongs))
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSingleSongScreen(song))
    }

    fun addToFavorite(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songsRepository.addToFavorite(song, isFavorite)
    }

    val searchQuery = MutableStateFlow("")
    val searchQueryLiveData = searchQuery.asLiveData()

    private val _bandsFlow = searchQuery.flatMapLatest { query ->
        songsRepository.getBandList(query).map { songList ->
            songList.toList()
        }
    }

    val bandsFlow = _bandsFlow


    private var songsJob: Job? = null

    // Shared flow to trigger song list updates
    private val _triggerSongListUpdate = MutableSharedFlow<Unit>(replay = 1)

    private val _songsFlow = _triggerSongListUpdate.flatMapLatest {
        searchQuery.flatMapLatest { query ->
            songsRepository.getSongList(query)
                .map { songList -> songList.toList() }
        }
    }
    val songsList = _songsFlow.asLiveData()

    fun startCollect() {
        Log.i("TAG", "startCollect")
        // Start collecting songs when triggered
        songsJob = viewModelScope.launch {
            _triggerSongListUpdate.emit(Unit)
        }
    }

    fun stopCollect() {
        Log.i("TAG", "stopCollect")
        // Stop collecting songs
        songsJob?.cancel()
        songsJob = null
    }

    val isCombinedListEmpty = _bandsFlow.combine(_songsFlow) { bandsList, songsList ->
        (bandsList.isEmpty() && songsList.isEmpty())
    }

    sealed class BandsEvent {
        data class NavigateToSongsListScreen(val band: String) : BandsEvent()
        data class NavigateToSingleSongScreen(val song: Song) : BandsEvent()
    }
}