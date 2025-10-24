package com.namangulati.studenthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
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

    fun addPapers(context: Context,paper: PapersModel, onResult: (Boolean) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val papersRef = database.getReference("notConfirmedPapers")
        val newRef = papersRef.push()

        newRef.setValue(paper)
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to upload paper: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }

            .addOnSuccessListener {
                onResult(true)
            }
    }
}
