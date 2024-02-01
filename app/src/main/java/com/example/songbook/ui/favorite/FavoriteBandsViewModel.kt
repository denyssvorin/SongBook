package com.example.songbook.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
class FavoriteBandsViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val searchQueryLiveData = searchQuery.asLiveData()

    private val favSongsFlow = searchQuery.flatMapLatest { query ->
        songDao.getBands(query).map { favSongList ->
            favSongList.filter {
                it.isFavorite
            }.map {
                it.bandName
            }.toSet().toList()
        }
    }

    val favBands = favSongsFlow.map {
        return@map it
    }.asLiveData()

    private val favBandsEventChannel = Channel<FavEvent>()
    val favBandsEvent = favBandsEventChannel.receiveAsFlow()

    fun onBandSelected(favBand: String) = viewModelScope.launch {
        favBandsEventChannel.send(FavEvent.NavigateToFavSongsScreen(favBand))
    }

    sealed class FavEvent() {
        data class NavigateToFavSongsScreen(val favBand: String) : FavEvent()
    }
}