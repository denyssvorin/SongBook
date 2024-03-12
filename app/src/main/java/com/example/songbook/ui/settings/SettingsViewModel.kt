package com.example.songbook.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.datastore.PreferencesManager
import com.example.songbook.data.datastore.entities.AppLanguagePreferences
import com.example.songbook.data.datastore.entities.AppThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
): ViewModel() {

    private val _appThemePreferencesFlow: Flow<AppThemePreferences> = preferencesManager.appThemePreferences

    val appThemePreferencesFlow: StateFlow<AppThemePreferences> = _appThemePreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AppThemePreferences(theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    )

    private val _appLanguagePreferencesFlow: Flow<AppLanguagePreferences> = preferencesManager.appLanguagePreferences

    val appLanguagePreferencesFlow: StateFlow<AppLanguagePreferences> =
        _appLanguagePreferencesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            AppLanguagePreferences(language = "en")
        )

    fun saveLanguagePreferences(language: String) = viewModelScope.launch {
        preferencesManager.saveAppLanguage(language)
    }

    fun saveThemePreferences(theme: Int) = viewModelScope.launch {
        preferencesManager.saveAppTheme(theme)
    }
}