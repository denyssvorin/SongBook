package com.example.songbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BandDao {

    @Query("SELECT * FROM band_table")
    fun getBands() : Flow<List<Band>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(band: Band)

    @Update
    suspend fun update(band: Band)

    @Delete
    suspend fun delete(band: Band)
}