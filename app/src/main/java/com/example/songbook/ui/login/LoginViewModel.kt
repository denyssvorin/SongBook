package com.example.songbook.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songbook.repo.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val songsRepository: SongsRepository
): ViewModel() {

    fun createPersonalFirestoreDocument(uid: String) = viewModelScope.launch {
        songsRepository.createPersonalFirestoreDocument(uid)
    }
}