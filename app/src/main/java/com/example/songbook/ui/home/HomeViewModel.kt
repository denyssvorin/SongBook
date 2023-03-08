package com.example.songbook.ui.home

import androidx.lifecycle.*
import com.example.songbook.data.AppDao
import com.example.songbook.data.Band
import com.example.songbook.data.relations.BandWithSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDao: AppDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val bandsFlow = searchQuery.flatMapLatest { appDao.getBandWithSongs(it) }
    val bands = bandsFlow.asLiveData()

    private val bandsEventChannel = Channel<BandsEvent>()
    val bandsEvent = bandsEventChannel.receiveAsFlow()


    fun onBandSelected(bandWithSongs: BandWithSongs) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSongsListScreen(bandWithSongs))
    }

    sealed class BandsEvent {
        data class NavigateToSongsListScreen(val bandWithSongs: BandWithSongs): BandsEvent()
    }
}