package com.example.songbook.ui.singleSong

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.AppDao
import com.example.songbook.data.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}