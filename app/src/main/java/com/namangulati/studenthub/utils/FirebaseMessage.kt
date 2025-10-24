package com.namangulati.studenthub.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.namangulati.studenthub.models.Message

object FirebaseMessage {
    fun sendMessage(context: Context,message: Message,senderRoom:String,receiverRoom:String ){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("chats")
        usersRef.child(senderRoom).child("messages").push()
            .setValue(message).addOnSuccessListener {
                usersRef.child(receiverRoom).child("messages").push()
                    .setValue(message)
            }
    }



}