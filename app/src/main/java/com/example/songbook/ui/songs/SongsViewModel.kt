package com.example.songbook.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import com.example.songbook.ui.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {
    
    val songs = songDao.getSongs().asLiveData()

    private val songsEventChannel = Channel<SongsEvent>()
    val songsEvent = songsEventChannel.receiveAsFlow()


    fun onSongSelected(song: Song) = viewModelScope.launch {
        songsEventChannel.send(SongsEvent.NavigateToSingleSongScreen(song))
    }

    sealed class SongsEvent {
        data class NavigateToSingleSongScreen(val song: Song) : SongsEvent()
    }
}