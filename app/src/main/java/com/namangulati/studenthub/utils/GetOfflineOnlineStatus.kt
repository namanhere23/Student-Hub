package com.namangulati.studenthub.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.namangulati.studenthub.models.UserDetailsModel

object GetOfflineOnlineStatus {

    fun getOfflineOnlineStatus(context: Context,uid:String,onResult: (String?) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val usersRef = database.collection("users")
        usersRef.document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserDetailsModel::class.java)
                if (user != null) {
                    if (user.status == "Online" && user.lastSeen != null) {
                        val currentTime = System.currentTimeMillis()
                        val diff = currentTime - user.lastSeen!!
                        if (diff > 15 * 60 * 1000) {
                            onResult("Offline")
                        } else {
                            onResult("Online")
                        }
                    } else {
                        onResult(user.status)
                    }
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                onResult("...")
            }
    }
}