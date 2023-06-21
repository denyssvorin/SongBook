package com.example.songbook.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    private val receivedDataList = mutableListOf<Song>()

    fun retrieveNewLatestData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            val databaseReference = Firebase.database.getReference("songs")

            // Read from the database
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    for (children: DataSnapshot in dataSnapshot.children) {
                        val song: Song? = children.getValue(Song::class.java)
                        song?.let { receivedDataList.add(it) }
                    }

                    for (song in receivedDataList) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val existingSong = songDao.getSongByName(song.songName)
                            val updatedSong = if (existingSong != null) {
                                existingSong.copy(
                                    bandName = song.bandName,
                                    isFavorite = existingSong.isFavorite,  // isFavorite value from database
                                    textSong = song.textSong
                                )
                            } else {
                                song
                            }
                            songDao.insertSong(updatedSong)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        }
    }
}