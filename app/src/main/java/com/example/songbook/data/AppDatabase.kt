package com.example.songbook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.songbook.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Band::class, Song::class], version = 1 )
abstract class AppDatabase: RoomDatabase() {

    abstract fun bandDao(): BandDao

    abstract fun songsDao(): SongDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val bandDao = database.get().bandDao()

            applicationScope.launch {
                bandDao.insert(Band("some text"))
                bandDao.insert(Band("some group"))
                bandDao.insert(Band("some band"))
                bandDao.insert(Band("Band"))
                bandDao.insert(Band("Band1"))
                bandDao.insert(Band("Band2"))
                bandDao.insert(Band("Band3"))
                bandDao.insert(Band("Band4"))
                bandDao.insert(Band("Band5"))
                bandDao.insert(Band("Band6"))
                bandDao.insert(Band("Band7"))
                bandDao.insert(Band("Band8"))
                bandDao.insert(Band("Band9"))
                bandDao.insert(Band("Band10"))
                bandDao.insert(Band("Band11"))
                bandDao.insert(Band("Band12"))
                bandDao.insert(Band("Band13"))
                bandDao.insert(Band("Band14"))
                bandDao.insert(Band("Band14"))
                bandDao.insert(Band("some text"))
                bandDao.insert(Band("some tre"))
                bandDao.insert(Band("some texwdfvt"))
            }

            val songDao = database.get().songsDao()

            applicationScope.launch {
                songDao.insert(Song("some text1"))
                songDao.insert(Song("some group11"))
                songDao.insert(Song("some Song111"))
                songDao.insert(Song("Song"))
                songDao.insert(Song("Song1"))
                songDao.insert(Song("Song2"))
                songDao.insert(Song("Song3"))
                songDao.insert(Song("Song4"))
                songDao.insert(Song("Song5"))
                songDao.insert(Song("Song6"))
                songDao.insert(Song("Song7"))
                songDao.insert(Song("Song8"))
                songDao.insert(Song("Song9"))
                songDao.insert(Song("Song10"))
                songDao.insert(Song("Song11"))
                songDao.insert(Song("Song12"))
                songDao.insert(Song("Song13"))
                songDao.insert(Song("Song14"))
                songDao.insert(Song("Song14"))
                songDao.insert(Song("some text345"))
                songDao.insert(Song("some tre324"))
                bandDao.insert(Band("some texwdfvt234"))

            }
        }
    }
}