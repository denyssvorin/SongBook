package com.example.songbook.ui.singlesong

import androidx.lifecycle.*
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class SingleSongViewModel @Inject constructor(
    private val songDao: SongDao
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

    private val _isScrollIcon = MutableLiveData(false)
    val isScrollIcon: LiveData<Boolean> = _isScrollIcon

    fun switchPlayIcon() {
        _isScrollIcon.value = _isScrollIcon.value?.not()
    }

    fun setPlayIconValue(booleanValue: Boolean) {
        _isScrollIcon.value = booleanValue
    }

    private val _isUserScroll = MutableLiveData(false)
    val isUserScroll : LiveData<Boolean> = _isUserScroll

    fun switchUserScrollValue() {
        _isUserScroll.value = _isUserScroll.value?.not()
    }

    fun setUserScrollValue(booleanValue: Boolean) {
        _isUserScroll.value = booleanValue
    }

    var customAnimationScrollSpeed = 20_000

    private val _scrollSpeedLayoutVisibilityStatus = MutableLiveData(true)
    val scrollSpeedLayoutVisibilityStatus: LiveData<Boolean> = _scrollSpeedLayoutVisibilityStatus

    fun switchScrollSpeedVisibility() {
        _scrollSpeedLayoutVisibilityStatus.value = _scrollSpeedLayoutVisibilityStatus.value?.not()
    }

    fun setScrollSpeedVisibility(visibility: Boolean) {
        _scrollSpeedLayoutVisibilityStatus.value = visibility
    }

    private fun addToFavoriteSong(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }
}