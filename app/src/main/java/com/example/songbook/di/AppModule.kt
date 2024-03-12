package com.example.songbook.di

import android.content.Context
import com.example.songbook.data.datastore.PreferencesManager
import com.example.songbook.data.datastore.PreferencesManagerImpl
import com.example.songbook.repo.SongsRepository
import com.example.songbook.repo.SongsRepositoryImpl
import com.example.songbook.ui.login.GoogleAuthClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideGoogleAuthClient(@ApplicationContext context: Context) =
        GoogleAuthClient(context, Identity.getSignInClient(context))

    @Provides
    fun providePreferences(@ApplicationContext context: Context):
        PreferencesManager = PreferencesManagerImpl(context)
    @Provides
    fun provideSongsRepository(
        firestore: FirebaseFirestore,
        @ApplicationScope applicationScope: CoroutineScope
    ): SongsRepository = SongsRepositoryImpl(firestore, applicationScope)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope