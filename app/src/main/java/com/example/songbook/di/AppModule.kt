package com.example.songbook.di

import android.app.Application
import androidx.room.Room
import com.example.songbook.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
       callback: AppDatabase.Callback
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "songbook_database")
        // Room permanently deletes all data from the tables in your database
        // when it attempts to perform a migration with no defined migration path
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun provideBandDao(db: AppDatabase) = db.appDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope