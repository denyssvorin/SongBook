package com.example.songbook.repo

import android.util.Log
import com.example.songbook.data.Song
import com.example.songbook.data.SongDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val songDao: SongDao
) : SongsRepository {
    private val receivedDataList = mutableListOf<Song>()
    private val databaseReference = Firebase.database.getReference("songs")
    private val uid = Firebase.auth.uid

//    override suspend fun createPersonalFirestoreDocument(uid: String) {
//        Log.i(TAG, "createPersonalFirestoreDocument: uid = $uid")
//
//        val personalDocumentReference = firestore
//            .collection(COLLECTION_USERS)
//            .document(uid)
//
//        personalDocumentReference.get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    return@addOnSuccessListener
//                } else {
//                    // create new document with current time
//                    val userData = hashMapOf(
//                        "createdAt" to FieldValue.serverTimestamp()
//                    )
//                    personalDocumentReference.set(userData)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "createPersonalFirestoreDocument: ${exception.message}")
//            }
//    }

    // Read from the database
    override fun fetchDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (children: DataSnapshot in dataSnapshot.children) {
                    val song: Song? = children.getValue(Song::class.java)
                    song?.let { receivedDataList.add(it) }
                }

                for (song in receivedDataList) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val existingSong = songDao.getSongByName(song.songName).first()
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
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        const val TAG = "SongsRepositoryImpl"
    }
}