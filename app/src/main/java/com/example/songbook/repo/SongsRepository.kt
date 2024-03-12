package com.example.songbook.repo

interface SongsRepository {

//    suspend fun createPersonalFirestoreDocument(uid: String)
    fun fetchDataFromFirebase()
}