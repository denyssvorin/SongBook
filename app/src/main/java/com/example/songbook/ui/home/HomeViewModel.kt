package com.example.songbook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.Band
import com.example.songbook.data.BandDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bandDao: BandDao
) : ViewModel() {

    val bands = bandDao.getBands().asLiveData()

    private val bandsEventChannel = Channel<BandsEvent>()
    val bandsEvent = bandsEventChannel.receiveAsFlow()


    fun onBandSelected(band: Band) = viewModelScope.launch {
        bandsEventChannel.send(BandsEvent.NavigateToSongsListScreen(band))
    }

    sealed class BandsEvent {
        data class NavigateToSongsListScreen(val band: Band): BandsEvent()
    }
}