package com.example.songbook.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.songbook.repo.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteBandsViewModel @Inject constructor(
    private val songsRepository: SongsRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val searchQueryLiveData = searchQuery.asLiveData()

    private val _favBandsFlow = searchQuery.flatMapLatest { query ->
        songsRepository.getFavoriteBandList(query)
    }

    val favBands = _favBandsFlow.asLiveData()

    val isFavListEmpty = _favBandsFlow.map { favBandList -> favBandList.isEmpty() }

    private val favBandsEventChannel = Channel<FavEvent>()
    val favBandsEvent = favBandsEventChannel.receiveAsFlow()

    fun onBandSelected(favBand: String) = viewModelScope.launch {
        favBandsEventChannel.send(FavEvent.NavigateToFavSongsScreen(favBand))
    }

    sealed class FavEvent() {
        data class NavigateToFavSongsScreen(val favBand: String) : FavEvent()
    }
}