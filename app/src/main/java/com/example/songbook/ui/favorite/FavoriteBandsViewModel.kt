package com.example.songbook.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.BandDao
import com.example.songbook.data.relations.BandWithSongs
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
    private val bandDao: BandDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val favBandsFlow = searchQuery.flatMapLatest {
        bandDao.getBandWithSongs(it).map { mainList ->
            mainList.filter { bandWithSongs ->
                bandWithSongs.songs.map { song -> song.isFavorite }.contains(true)
            }.map { favBand ->
                favBand.copy(songs = favBand.songs.filter { it.isFavorite })
            }
        }
    }
    val favBands = favBandsFlow.asLiveData()

    private val favBandsEventChannel = Channel<FavEvent>()
    val favBandsEvent = favBandsEventChannel.receiveAsFlow()

    fun onBandSelected(bandWithSongs: BandWithSongs) = viewModelScope.launch {
        favBandsEventChannel.send(FavEvent.NavigateToFavSongsScreen(bandWithSongs))
    }

    sealed class FavEvent() {
        data class NavigateToFavSongsScreen(val bandWithSongs: BandWithSongs) : FavEvent()
    }
}