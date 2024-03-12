package com.example.songbook.repo

import android.util.Log
import com.example.songbook.data.Song
import com.example.songbook.di.ApplicationScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationScope private val applicationScope: CoroutineScope
) : SongsRepository {
    private val uid = Firebase.auth.uid
    override suspend fun createPersonalFirestoreDocument(uid: String) {
        Log.i(TAG, "createPersonalFirestoreDocument: uid = $uid")

        val personalDocumentReference = firestore
            .collection(COLLECTION_USERS)
            .document(uid)

        personalDocumentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    return@addOnSuccessListener
                } else {
                    // create new document with current time
                    val userData = hashMapOf(
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    personalDocumentReference.set(userData)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "createPersonalFirestoreDocument: ${exception.message}")
            }
    }

    override fun getBandList(searchQuery: String): Flow<List<String>> = callbackFlow {
        val bandList = CopyOnWriteArrayList<String>()

        val firestoreBandsRef = firestore.collection(COLLECTION_BANDS)
        val listener = firestoreBandsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            val source = if (snapshot?.metadata?.isFromCache == true) {
                "local cache"
            } else {
                "server"
            }
            Log.i(TAG, "BandList fetched from $source")

            snapshot?.let {
                for (band in it.documents) {

                    if (band.id.contains(searchQuery, true)) {
                        bandList.add(band.id)
                    }
                }
                trySend(bandList).isSuccess
            }
        }
        awaitClose {
            Log.i(TAG, "getBandList awaitClose")
            listener.remove()
        }
    }

    override fun getSongList(searchQuerySongName: String): Flow<List<Song>> = callbackFlow {
        val songMap = ConcurrentHashMap<String, Song>()

        val firestoreBandsRef = firestore.collection(COLLECTION_BANDS)
        val listener = firestoreBandsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                for (band in querySnapshot.documents) {
                    val songsRef = band.reference.collection(COLLECTION_SONGS)
                    songsRef.get().addOnSuccessListener { songsSnapshot ->
                        for (songDocument in songsSnapshot.documents) {
                            val bandName = band.id
                            val songName = songDocument.id

                            if (songName.contains(searchQuerySongName, true)) {

                                applicationScope.launch {
                                    getFavoriteValue(
                                        bandName = bandName,
                                        songName = songName
                                    ).collect { isFavoriteValue ->
                                        Log.i(TAG, "getSongList: isFavorite = $isFavoriteValue")

                                        songMap.computeIfAbsent(songName) {
                                            Song(
                                                songName = songName,
                                                bandName = bandName,
                                                isFavorite = isFavoriteValue
                                            )
                                        }

                                        Log.i(
                                            TAG,
                                            "getSongList: ${
                                                songMap.values.toList().sortedBy { it.songName }
                                            }"
                                        )
                                        Log.i(
                                            TAG,
                                            "getSongListSize: ${
                                                songMap.values.toList()
                                                    .sortedBy { it.songName }.size
                                            }"
                                        )

                                        trySend(
                                            songMap.values.toList()
                                                .sortedBy { it.songName }).isSuccess
                                    }
                                }
                            } else {
                                // no matches with user search query
                                trySend(songMap.values.toList()).isSuccess
                            }
                        }
                    }.addOnFailureListener { e ->
                        close(e)
                    }
                }
            }
        }
        awaitClose {
            listener.remove()
            Log.i(TAG, "getSongList awaitClose")
        }
    }

    override fun getSongsOfTheBand(query: String, bandName: String): Flow<List<Song>> =
        callbackFlow {
            val songMap = mutableMapOf<String, Boolean>() // Map to save isFavorite for each song
            val mutex = Mutex() // Mutex to synchronize access to the song map

            val firestoreSongsRef = firestore
                .collection(COLLECTION_BANDS)
                .document(bandName)
                .collection(COLLECTION_SONGS)

            val listener = firestoreSongsRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    for (songDocument in snapshot.documents) {
                        if (songDocument.id.contains(query, true)) {
                            val songName = songDocument.id

                            applicationScope.launch {
                                getFavoriteValue(
                                    bandName = bandName,
                                    songName = songName
                                ).collect { isFavoriteValue ->
                                    mutex.withLock {
                                        // Update the isFavorite value for a song
                                        songMap[songName] = isFavoriteValue

                                        // Create a list of songs to send to the collector
                                        val songsToSend = songMap
                                            .keys
                                            .sorted()
                                            .map { songMapSongName ->
                                                Song(
                                                    songName = songMapSongName,
                                                    bandName = bandName,
                                                    isFavorite = songMap[songMapSongName] ?: false
                                                )
                                            }

                                        trySend(songsToSend).isSuccess
                                    }
                                }
                            }
                        }
                    }
                }
            }
            awaitClose {
                listener.remove()
                Log.i(TAG, "getSongsOfTheBand awaitClose")
            }
        }

    override fun getSingleSong(bandName: String, songName: String): Flow<Song> = callbackFlow {
        uid?.let {
            val firestoreSingleSongRef = firestore
                .collection(COLLECTION_BANDS)
                .document(bandName)
                .collection(COLLECTION_SONGS)
                .document(songName)

            val listener = firestoreSingleSongRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val songText = snapshot[TEXT_OF_SONG] as String
                    val firstFormattingSongText = songText.replace("/n/n", "\n\n")
                    val secondFormattingSongText = firstFormattingSongText.replace("/n", "\n")

                    val song = Song(
                        bandName = bandName,
                        songName = songName,
                        textSong = secondFormattingSongText
                    )
                    applicationScope.launch {
                        getFavoriteValue(
                            bandName = bandName,
                            songName = songName
                        ).collect { favoriteValue ->

                            song.copy(
                                isFavorite = favoriteValue
                            )
                            trySend(song).isSuccess

                            Log.i(TAG, "getSongText: songText = $songText")
                        }
                    }
                }
            }
            awaitClose {
                listener.remove()
                Log.i(TAG, "getSingleSong awaitClose")
            }
        }
    }

    override fun getFavoriteValue(bandName: String, songName: String): Flow<Boolean> =
        callbackFlow {
            uid?.let { userId ->
                val firestoreUserRef = firestore
                    .collection(COLLECTION_USERS)
                    .document(userId)

                val listener = firestoreUserRef.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        close(exception)
                        return@addSnapshotListener
                    }

                    // validation whether user personal collection is created or not
                    if (snapshot?.exists()!!) {
                        val query = FieldPath.of("$bandName/$songName")
                        val isFavoriteValue = snapshot[query] as? Boolean ?: false
                        trySend(isFavoriteValue).isSuccess
                    } else {
                        // create new document
                        applicationScope.launch {
                            createPersonalFirestoreDocument(uid)
                        }
                        trySend(false).isSuccess
                    }
                }
                awaitClose {
                    listener.remove()
                    Log.i(TAG, "getFavoriteValue awaitClose")
                }
            }
        }

    override fun addToFavorite(song: Song, isFavorite: Boolean) {
        uid?.let { userId ->
            val docRef = firestore.collection(COLLECTION_USERS).document(userId)
            val isFavoriteKey = FieldPath.of("${song.bandName}/${song.songName}")
            docRef.update(isFavoriteKey, isFavorite)
                .addOnSuccessListener {
                    Log.i(TAG, "addToFavorite: success")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "addToFavorite: ${e.message}")
                }
        }
    }
    override fun getFavoriteBandList(searchQuery: String): Flow<List<String>> = callbackFlow {
        val bandList = CopyOnWriteArrayList<String>()
        val mutex = Mutex()

        uid?.let {
            val firestoreBandsRef = firestore.collection(COLLECTION_BANDS)

            val listener = firestoreBandsRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    for (band in it.documents) {
                        val bandName = band.id
                        Log.i(TAG, "getFavoriteBandList: bandName = $bandName")

                        if (bandName.contains(searchQuery, true)) {
                            applicationScope.launch {
                                getFavoriteSongsOfTheBand(
                                    "",
                                    bandName = bandName
                                ).collect { songList ->
                                    if (songList.isNotEmpty()) {
                                        mutex.withLock {
                                            for (favSong in songList) {
                                                if (!bandList.contains(favSong.bandName)) {
                                                    bandList.add(favSong.bandName)
                                                }
                                            }
                                            val listToSend = bandList.sorted().toSet().toList()
                                            trySend(listToSend).isSuccess
                                        }
                                    } else {
                                        mutex.withLock {
                                            bandList.remove(bandName)
                                            val listToSend = bandList.sorted().toSet().toList()
                                            trySend(listToSend).isSuccess
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            awaitClose {
                Log.i(TAG, "getFavoriteBandList awaitClose")
                listener.remove()
            }
        }
    }

    override fun getFavoriteSongsOfTheBand(
        searchQuery: String,
        bandName: String
    ): Flow<List<Song>> =
        callbackFlow {
            val songMap =
                mutableMapOf<String, Boolean>() // Map to save isFavorite for each song
            val mutex = Mutex() // Mutex to synchronize access to the song map

            uid?.let {
                val firestoreSongsRef = firestore
                    .collection(COLLECTION_BANDS)
                    .document(bandName)
                    .collection(COLLECTION_SONGS)

                val listener = firestoreSongsRef.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        close(exception)
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        for (songDocument in snapshot.documents) {
                            if (songDocument.id.contains(searchQuery, true)) {
                                val songName = songDocument.id

                                applicationScope.launch {
                                    getFavoriteValue(
                                        bandName = bandName,
                                        songName = songName
                                    ).collect { isFavoriteValue ->
                                        mutex.withLock {
                                            // Update the isFavorite value for a song
                                            songMap[songName] = isFavoriteValue

                                            // Filter and sort the song list to send it to the collector
                                            val songsToSend = songMap
                                                .filter { it.value } // Only favorite songs
                                                .keys
                                                .sorted()
                                                .map {
                                                    Song(
                                                        songName = it,
                                                        bandName = bandName,
                                                        isFavorite = true
                                                    )
                                                }
                                            trySend(songsToSend).isSuccess
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                awaitClose {
                    listener.remove()
                    Log.i(TAG, "getSongsOfTheBand awaitClose")
                }
            }
        }

    companion object {
        private const val COLLECTION_BANDS = "bands"
        private const val COLLECTION_SONGS = "songs"
        private const val TEXT_OF_SONG = "textSong"

        private const val COLLECTION_USERS = "users"
        private const val TAG = "SongsRepositoryImpl"

    }
}