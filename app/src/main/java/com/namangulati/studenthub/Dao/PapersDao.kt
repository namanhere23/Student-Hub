package com.namangulati.studenthub.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.namangulati.studenthub.modelsRoom.PapersModelRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface PapersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(papers: List<PapersModelRoom>)

    @Query("Select * from PapersModelRoom")
    fun getAllPapers(): Flow<List<PapersModelRoom>>

    @Query("SELECT * FROM PapersModelRoom WHERE semester = :sem")
    fun getPapersBySem(sem: Int): Flow<List<PapersModelRoom>>

    @Query("DELETE FROM PapersModelRoom")
    suspend fun deleteAllPapers()
}