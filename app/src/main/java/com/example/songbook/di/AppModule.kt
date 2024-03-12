package com.example.songbook.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.songbook.data.AppDatabase
import com.example.songbook.data.SongDao
import com.example.songbook.datastore.PreferencesManager
import com.example.songbook.datastore.PreferencesManagerImpl
import com.example.songbook.repo.SongsRepository
import com.example.songbook.repo.SongsRepositoryImpl
import com.example.songbook.ui.login.GoogleAuthClient
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(
        app: Application,
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "songbook_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideSongDao(db: AppDatabase) = db.songDao()

    @Provides
    fun provideGoogleAuthClient(@ApplicationContext context: Context) =
        GoogleAuthClient(context, Identity.getSignInClient(context))
    @Provides
    fun providePreferences(@ApplicationContext context: Context):
            PreferencesManager = PreferencesManagerImpl(context)

    @Provides
    fun provideSongsRepository(dao: SongDao): SongsRepository = SongsRepositoryImpl(dao)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope