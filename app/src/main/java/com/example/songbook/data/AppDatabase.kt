package com.example.songbook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.songbook.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Song::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val songDao = database.get().songDao()

            applicationScope.launch {
                songDao.insertSong(
                    Song(
                        "Band1",
                        "song",
                        textSong = "Lorem ipsum dolor sit amet, " +
                                "\nconsectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                                "\n\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
                                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat " +
                                "nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                                "mollit anim id est laborum"
                    )
                )
                songDao.insertSong(Song("Band1", "Long story"))
                songDao.insertSong(Song("Band2", "sdkflk"))
                songDao.insertSong(
                    Song(
                        "Band2",
                        "With text",
                        textSong = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                                "\n\nLorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                                "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                                "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                                "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset " +
                                "sheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                                "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                                "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                                "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset " +
                                "sheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                                "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                                "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                                "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset " +
                                "\n\nsheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                                "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                                "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                                "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset " +
                                "sheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                                "like Aldus PageMaker including versions of Lorem Ipsum."
                    )
                )
                songDao.insertSong(Song("Band2", "somem"))
                songDao.insertSong(Song("Band2", "Favoured", true))
                songDao.insertSong(Song("Band3", "Song"))
                songDao.insertSong(Song("Band4", "Sonn", true))
                songDao.insertSong(Song("Band5", "fav by default", true))
            }
        }
    }
}