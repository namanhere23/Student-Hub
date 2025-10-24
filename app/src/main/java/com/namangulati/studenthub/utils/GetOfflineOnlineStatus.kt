package com.namangulati.studenthub.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.namangulati.studenthub.models.UserDetailsModel

object GetOfflineOnlineStatus {

    fun getOfflineOnlineStatus(context: Context,uid:String,onResult: (String?) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        usersRef.child(uid).get()
            .addOnSuccessListener { snap ->
                val user = snap.getValue(UserDetailsModel::class.java)
                onResult(user?.status)
            }
            .addOnFailureListener { e ->
                onResult("...")
            }
    }
}