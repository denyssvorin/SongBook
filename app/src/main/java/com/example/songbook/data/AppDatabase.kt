package com.example.songbook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.songbook.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Band::class, Song::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appDao(): AppDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val appDao = database.get().appDao()

            val bands: List<Band> = listOf(
                Band("Band1"),
                Band("Band1"),
                Band("Band2"),
                Band("Band3"),
                Band("Band4"),
                Band("Band5"),
                Band("Band6")
            )

            applicationScope.launch {
                bands.forEach { appDao.insertBand(it) }
            }

            applicationScope.launch {
                appDao.insertSong(Song("some text1","Band1", textSong = "Lorem ipsum dolor sit amet, \nconsectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"))
                appDao.insertSong(Song("some group11","Band1"))
                appDao.insertSong(Song("some Song111", "Band2"))
                appDao.insertSong(Song("some Song", "Band2"))
                appDao.insertSong(Song("main song", "Band2"))
                appDao.insertSong(Song("something that fav", "Band2", true))
                appDao.insertSong(Song("Song", "Band3"))
                appDao.insertSong(Song("Song1", "Band4", true))
                appDao.insertSong(Song("Song2", "Band5", true))
            }
        }
    }
}