package com.example.songbook.datastore

import com.example.songbook.datastore.entities.AppLanguagePreferences
import com.example.songbook.datastore.entities.AppThemePreferences
import com.example.songbook.datastore.entities.SingleSongScrollAnimationPreferences
import com.example.songbook.datastore.entities.SingleSongTextSizePreferences
import kotlinx.coroutines.flow.Flow


interface PreferencesManager {

    val appThemePreferences: Flow<AppThemePreferences>
    val appLanguagePreferences: Flow<AppLanguagePreferences>

    val singleSongScrollAnimationPreferences: Flow<SingleSongScrollAnimationPreferences>
    val singleSongTextSizePreferences: Flow<SingleSongTextSizePreferences>

    suspend fun saveAppLanguage(newLanguage: String)

    suspend fun saveAppTheme(newThemeMode: Int)

    suspend fun saveSingleSongTextSize(newTextSize: Int)

    suspend fun saveSingleSongAnimationSpeedValue(newAnimationSpeedValue: Int)

    suspend fun saveSingleSongScrollLayoutVisibility(visibility: Boolean)
}