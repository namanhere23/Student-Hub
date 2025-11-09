package com.namangulati.studenthub.Database

import android.content.Context
import com.namangulati.studenthub.modelsRoom.PapersModelRoom
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PapersRepository(private val context: Context) {

    private val dao = PapersDatabase.getInstance(context).dao
    fun getLocalPapers(): Flow<List<PapersModelRoom>> {
        return dao.getAllPapers()
    }

    suspend fun syncPapersFromFirebase() {
        FirebasePapersDatabaseUtils.loadAllPapers(context) { papersFromFirebase ->
            CoroutineScope(Dispatchers.IO).launch {
                val roomPapers = papersFromFirebase.map {
                    PapersModelRoom(
                        subject = it.subject,
                        semester = it.semester,
                        year = it.year,
                        exam = it.exam,
                        link = it.link
                    )
                }

                dao.deleteAllPapers()
                dao.insertMany(roomPapers)
            }
        }
    }
}