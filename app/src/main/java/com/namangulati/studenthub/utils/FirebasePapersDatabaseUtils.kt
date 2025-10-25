package com.namangulati.studenthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.namangulati.studenthub.models.NotConfirmedPapersModel
import com.namangulati.studenthub.models.PapersModel

object FirebasePapersDatabaseUtils {

    fun loadAllPapers(context:Context, onResult: (List<PapersModel>) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val papersRef = database.getReference("papers")

        papersRef.get()
            .addOnSuccessListener { snap->
                val papers = snap.children.mapNotNull { it.getValue(PapersModel::class.java) }
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

        val database = FirebaseDatabase.getInstance()
        val papersRef = database.getReference("notConfirmedPapers")
        val newRef = papersRef.push()
        paper.key = newRef.key
        newRef.setValue(paper)
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

        val database = FirebaseDatabase.getInstance()
        val papersRef = database.getReference("papers")
        val newRef = papersRef.push()

        newRef.setValue(paper)
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

        val database = FirebaseDatabase.getInstance()
        val paperRef = database.getReference("notConfirmedPapers").child(paperId)

        paperRef.removeValue()
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

        val database = FirebaseDatabase.getInstance()
        val papersRef = database.getReference("notConfirmedPapers")

        papersRef.get()
            .addOnSuccessListener { snap->
                val papers = snap.children.mapNotNull { it.getValue(NotConfirmedPapersModel::class.java) }
                onResult(papers)
            }
            .addOnFailureListener{
                onResult(emptyList())
            }
    }


}
