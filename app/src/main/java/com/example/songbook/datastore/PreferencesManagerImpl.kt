package com.example.songbook.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.songbook.datastore.entities.AppLanguagePreferences
import com.example.songbook.datastore.entities.AppThemePreferences
import com.example.songbook.datastore.entities.SingleSongScrollAnimationPreferences
import com.example.songbook.datastore.entities.SingleSongTextSizePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesManager {

    override val appThemePreferences: Flow<AppThemePreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("TAG", "Error message ${exception.message}")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences: Preferences ->
            val theme = preferences[APP_THEME_KEY] ?: -1
            AppThemePreferences(theme)
        }

    override val appLanguagePreferences: Flow<AppLanguagePreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("TAG", "Error message ${exception.message}")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences: Preferences ->
            val language = preferences[APP_LANGUAGE_KEY] ?: "en"
            AppLanguagePreferences(language)
        }

    override val singleSongScrollAnimationPreferences: Flow<SingleSongScrollAnimationPreferences> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("TAG", "Error message ${exception.message}")
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences: Preferences ->
                val scrollAnimationSpeed =
                    preferences[SINGLE_SONG_ANIMATION_SPEED_KEY] ?: 50_000
                val scrollLayoutVisibility =
                    preferences[SINGLE_SONG_SCROLL_LAYOUT_VISIBILITY_KEY] ?: true
                SingleSongScrollAnimationPreferences(scrollAnimationSpeed, scrollLayoutVisibility)
            }

    override val singleSongTextSizePreferences: Flow<SingleSongTextSizePreferences> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("TAG", "Error message ${exception.message}")
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences: Preferences ->
                val textSize = preferences[SINGLE_SONG_TEXT_SIZE_KEY] ?: 16
                SingleSongTextSizePreferences(textSize)
            }


    override suspend fun saveAppLanguage(newLanguage: String) {
        context.dataStore.edit { settings ->
            settings[APP_LANGUAGE_KEY] = newLanguage
        }
    }

    override suspend fun saveAppTheme(newThemeMode: Int) {
        context.dataStore.edit { settings ->
            settings[APP_THEME_KEY] = newThemeMode
        }
    }

    override suspend fun saveSingleSongTextSize(newTextSize: Int) {
        context.dataStore.edit { settings ->
            settings[SINGLE_SONG_TEXT_SIZE_KEY] = newTextSize
        }
    }

    override suspend fun saveSingleSongAnimationSpeedValue(newAnimationSpeedValue: Int) {
        context.dataStore.edit { settings ->
            settings[SINGLE_SONG_ANIMATION_SPEED_KEY] = newAnimationSpeedValue
        }
    }

    override suspend fun saveSingleSongScrollLayoutVisibility(visibility: Boolean) {
        context.dataStore.edit { settings ->
            settings[SINGLE_SONG_SCROLL_LAYOUT_VISIBILITY_KEY] = visibility
        }
    }

    companion object PrefKeys {
        val APP_LANGUAGE_KEY = stringPreferencesKey("APP_LANGUAGE_KEY")
        val APP_THEME_KEY = intPreferencesKey("APP_THEME_KEY")

        val SINGLE_SONG_TEXT_SIZE_KEY = intPreferencesKey("SONG_TEXT_SIZE_KEY")
        val SINGLE_SONG_ANIMATION_SPEED_KEY = intPreferencesKey("SONG_ANIMATION_SPEED_KEY")
        val SINGLE_SONG_SCROLL_LAYOUT_VISIBILITY_KEY =
            booleanPreferencesKey("SINGLE_SONG_SCROLL_LAYOUT_VISIBILITY_KEY")
    }
}