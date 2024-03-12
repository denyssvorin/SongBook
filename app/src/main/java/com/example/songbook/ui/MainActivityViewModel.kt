package com.example.songbook.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songbook.datastore.PreferencesManager
import com.example.songbook.datastore.entities.AppLanguagePreferences
import com.example.songbook.datastore.entities.AppThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _appThemePreferencesFlow: Flow<AppThemePreferences> = preferencesManager.appThemePreferences

    val appThemePreferencesFlow: SharedFlow<AppThemePreferences> = _appThemePreferencesFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 0,
    )

    private val _appLanguagePreferencesFlow: Flow<AppLanguagePreferences> = preferencesManager.appLanguagePreferences

    val appLanguagePreferencesFlow: StateFlow<AppLanguagePreferences> = _appLanguagePreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AppLanguagePreferences(language = "en")
    )
}