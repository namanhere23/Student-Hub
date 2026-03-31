package com.namangulati.studenthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.namangulati.studenthub.Dao.PapersDao
import com.namangulati.studenthub.Database.PapersDatabase
import com.namangulati.studenthub.models.NotConfirmedPapersModel
import com.namangulati.studenthub.models.PapersModel
import androidx.lifecycle.lifecycleScope
import com.namangulati.studenthub.modelsRoom.PapersModelRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object FirebasePapersDatabaseUtils {

    fun loadAllPapers(context:Context, onResult: (List<PapersModel>) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val papersRef = database.collection("papers")

        papersRef.get()
            .addOnSuccessListener { snap->
                val papers = snap.toObjects(PapersModel::class.java)
                onResult(papers)
            }
            .addOnFailureListener{
                onResult(emptyList())
            }
    }

    fun addPapers(context: Context,paper: NotConfirmedPapersModel, onResult: (Boolean) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val papersRef = database.collection("notConfirmedPapers")
        val newRef = papersRef.document()
        paper.key = newRef.id
        newRef.set(paper)
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to upload paper: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
            .addOnSuccessListener {
                onResult(true)
            }
    }

    fun confirmPapers(context: Context,paper: NotConfirmedPapersModel, onResult: (Boolean) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val papersRef = database.collection("papers")
        val newRef = papersRef.document()

        newRef.set(paper)
            .addOnFailureListener { e ->
                deletePaper(context,paper.key!!){
                    onResult(true)
                }
                onResult(false)
            }
            .addOnSuccessListener {
                onResult(true)
            }
    }

    fun deletePaper(context: Context, paperId: String, onResult: (Boolean) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val paperRef = database.collection("notConfirmedPapers").document(paperId)

        paperRef.delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed to delete paper: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onResult(false)
            }
    }

    fun loadAllUnconfirmedPapers(context:Context,onResult: (List<NotConfirmedPapersModel>) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val papersRef = database.collection("notConfirmedPapers")

        papersRef.get()
            .addOnSuccessListener { snap->
                val papers = snap.toObjects(NotConfirmedPapersModel::class.java)
                onResult(papers)
            }
            .addOnFailureListener{
                onResult(emptyList())
            }
    }

    suspend fun loadPapersFromRoom(context:Context): List<PapersModelRoom>{
        val dao = PapersDatabase.getInstance(context).dao
        return dao.getAllPapers().first()
    }

}
