package com.example.songbook.ui.songs

import androidx.lifecycle.*
import com.example.songbook.data.AppDao
import com.example.songbook.data.Song
import com.example.songbook.data.relations.BandWithSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val appDao: AppDao
) : ViewModel() {

    private val _bandWithSongs = MutableLiveData<BandWithSongs>()
    val bandWithSongs: LiveData<BandWithSongs>
        get() = _bandWithSongs

    val searchQuery = MutableStateFlow("")
    private val songsFlow = searchQuery.flatMapLatest {
        appDao.getSongs(it)
    }
    //val songs = songsFlow.asLiveData()

    private val songsEventChannel = Channel<SongsEvent>()
    val songsEvent = songsEventChannel.receiveAsFlow()

    fun onBandLoaded(bandWithSongs: BandWithSongs) {
        _bandWithSongs.postValue(bandWithSongs)
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        songsEventChannel.send(SongsEvent.NavigateToSingleSongScreen(song))
    }

    fun addToFavorite(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        appDao.update(song.copy(isFavorite = isFavorite))
    }

    sealed class SongsEvent {
        data class NavigateToSingleSongScreen(val song: Song) : SongsEvent()
    }
}