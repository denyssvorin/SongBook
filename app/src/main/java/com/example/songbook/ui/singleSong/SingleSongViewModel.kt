package com.example.songbook.ui.singleSong

import androidx.lifecycle.*
import com.example.songbook.data.AppDao
import com.example.songbook.data.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SingleSongViewModel @Inject constructor(
    private val appDao: AppDao
) : ViewModel() {

    val resultSuccessFavorite = MutableLiveData<Boolean>()
    val resultDeleteFavorite = MutableLiveData<Boolean>()

    private var isFavorite = false

    fun setFavorite(song: Song) {
        viewModelScope.launch {
            song.let {
                if (isFavorite) {
                    appDao.delete(song)
                    resultDeleteFavorite.value = true
                } else {
                    appDao.insertSong(song)
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
        val flow = appDao.getTextSong(song)
        flow.collect { text ->
            _textSong.value = text
        }
    }
}