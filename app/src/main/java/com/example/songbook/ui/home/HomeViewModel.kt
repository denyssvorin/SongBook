package com.example.songbook.ui.home

import androidx.lifecycle.*
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
class HomeViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val bandsFlow = searchQuery.flatMapLatest { query ->
        songDao.getSongs(query).map { songList ->
            songList.map {
                it.bandName
            }.toSet().toList()
        }
    }

    val bands = bandsFlow.map {
        return@map it
    }.asLiveData()

    private val bandsEventChannel = Channel<BandsEvent>()
    val bandsEvent = bandsEventChannel.receiveAsFlow()


    fun onBandSelected(bandWithSongs: String) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSongsListScreen(bandWithSongs))
    }

    sealed class BandsEvent {
        data class NavigateToSongsListScreen(val band: String) : BandsEvent()
    }
}