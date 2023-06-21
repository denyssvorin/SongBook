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

    val resultSuccessFavorite = SingleLiveEvent<Boolean>()

    var isFavorite by Delegates.notNull<Boolean>()
    var isScrollIcon by Delegates.notNull<Boolean>()

    fun setFavorite(song: Song) {
        viewModelScope.launch {
            song.let {
                if (isFavorite) {
                    addToFavoriteSong(song, false)
                    resultSuccessFavorite.setValue(false)
                } else {
                    addToFavoriteSong(song, true)
                    resultSuccessFavorite.setValue(true)
                }
                isFavorite = !isFavorite
            }
        }
    }

    fun setPlayIcon() {
        isScrollIcon = !isScrollIcon
    }

    fun addToFavoriteSong(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }

    interface OnAddToFavoriteClickListener {
        fun addToFavorite(song: Song, isFavorite: Boolean)
    }
}