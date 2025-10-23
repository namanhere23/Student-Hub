package com.namangulati.studenthub.utils

import android.content.Context
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

}