package com.example.songbook.ui.singleSong

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

    val resultSuccessFavorite = MutableLiveData<Boolean>()
    val resultDeleteFavorite = MutableLiveData<Boolean>()

    var isFavorite by Delegates.notNull<Boolean>()

    fun setFavorite(song: Song) {
        viewModelScope.launch {
            song.let {
                if (isFavorite) {
                    addToFavoriteSong(song, false)
                    resultDeleteFavorite.value = true
                } else {
                    addToFavoriteSong(song, true)
                    resultSuccessFavorite.value = true
                }
            }
            isFavorite = !isFavorite
        }
    }

    private val _textSong = MutableLiveData<String>()
    val textSong: LiveData<String>
        get() = _textSong

    fun getSongBySongName(song: String) = viewModelScope.launch {
        val flow = songDao.getSongText(song)
        flow.collect { text ->
            _textSong.value = text
        }
    }

    fun addToFavoriteSong(song: Song, isFavorite: Boolean) = viewModelScope.launch {
        songDao.update(song.copy(isFavorite = isFavorite))
    }

    interface OnAddToFavoriteClickListener {
        fun addToFavorite(song: Song, isFavorite: Boolean)
    }
}