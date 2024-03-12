package com.example.songbook.ui.singlesong

import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import com.example.songbook.datastore.PreferencesManager
import com.example.songbook.datastore.entities.SingleSongScrollAnimationPreferences
import com.example.songbook.datastore.entities.SingleSongTextSizePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class SingleSongViewModel @Inject constructor(
    private val songDao: SongDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // value for process value from LiveData only once
    val favoriteDisposableValue = SingleLiveEvent<Boolean>()

    var isFavorite by Delegates.notNull<Boolean>()

    fun setFavorite(song: Song) {
        song.let {
            if (isFavorite) {
                addToFavoriteSong(song, false)
                favoriteDisposableValue.setValue(false)
            } else {
                addToFavoriteSong(song, true)
                favoriteDisposableValue.setValue(true)
            }
            isFavorite = !isFavorite
        }

    }

    private val _songName = MutableStateFlow(Song())

    fun getSong(song: Song) {
        _songName.value = song
    }

    private val _songFlow = _songName.flatMapLatest { song: Song ->
//        songsRepository.getSingleSong(bandName = song.bandName, songName = song.songName)
        songDao.getSongByName(song.songName)
    }
    val song = _songFlow.asLiveData()

    private val _isScrollIcon = MutableLiveData(false)
    val isScrollIcon: LiveData<Boolean> = _isScrollIcon

    fun switchPlayIcon() {
        _isScrollIcon.value = _isScrollIcon.value?.not()
    }

    fun setPlayIconValue(booleanValue: Boolean) {
        _isScrollIcon.value = booleanValue
    }

    private val _isUserScroll = MutableLiveData(false)
    val isUserScroll: LiveData<Boolean> = _isUserScroll

    fun switchUserScrollValue() {
        _isUserScroll.value = _isUserScroll.value?.not()
    }

    fun setUserScrollValue(booleanValue: Boolean) {
        _isUserScroll.value = booleanValue
    }

    var customAnimationScrollSpeed = 50_000

    private val _scrollSpeedLayoutVisibilityStatus = MutableLiveData(true)
    val scrollSpeedLayoutVisibilityStatus: LiveData<Boolean> = _scrollSpeedLayoutVisibilityStatus

    fun switchScrollSpeedLayoutVisibility() {
        _scrollSpeedLayoutVisibilityStatus.value = _scrollSpeedLayoutVisibilityStatus.value?.not()
    }

    fun setScrollSpeedLayoutVisibility(visibility: Boolean) {
        _scrollSpeedLayoutVisibilityStatus.value = visibility
    }

    private val _singleSongScrollAnimationPreferencesFlow: Flow<SingleSongScrollAnimationPreferences> =
        preferencesManager.singleSongScrollAnimationPreferences

    val singleSongScrollAnimationPreferencesFlow: StateFlow<SingleSongScrollAnimationPreferences> =
        _singleSongScrollAnimationPreferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SingleSongScrollAnimationPreferences(
                customAnimationScrollSpeed,
                _scrollSpeedLayoutVisibilityStatus.value!!
            )
        )

    fun saveScrollAnimationSpeed(value: Int) = viewModelScope.launch {
        preferencesManager.saveSingleSongAnimationSpeedValue(value)
    }

    fun saveScrollSpeedLayoutVisibility(value: Boolean) = viewModelScope.launch {
        preferencesManager.saveSingleSongScrollLayoutVisibility(value)
    }


    private val _singleSongTextSizePreferencesFlow: Flow<SingleSongTextSizePreferences> =
        preferencesManager.singleSongTextSizePreferences

    val singleSongTextSizePreferencesFlow: StateFlow<SingleSongTextSizePreferences> =
        _singleSongTextSizePreferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SingleSongTextSizePreferences(16)
        )

    fun saveTextSize(value: Int) = viewModelScope.launch {
        preferencesManager.saveSingleSongTextSize(value)
    }


    private fun addToFavoriteSong(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }
}