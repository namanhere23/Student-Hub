package com.namangulati.studenthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.namangulati.studenthub.models.ContactsModel
import com.namangulati.studenthub.models.UserDetailsModel

object FirebaseUserDatabaseUtils {

    fun loadUserByUid(context: Context, uid: String, onResult: (UserDetailsModel?) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        usersRef.child(uid).get()
            .addOnSuccessListener { snap ->
                val user = snap.getValue(UserDetailsModel::class.java)
                onResult(user)
            }
            .addOnFailureListener { e ->
                Log.e("Profile", "Failed to read user", e)
                Toast.makeText(context, "Read failed: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }

    fun saveUser(context:Context, user: UserDetailsModel , onResult: (Boolean) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        if (user.uid != null) {
            usersRef.child(user.uid!!).setValue(user)
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }

                .addOnSuccessListener {
                    onResult(true)
                }
        }
    }

    fun loadAllUsers(context: Context, onResult: (List<ContactsModel>) -> Unit){
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.get()
            .addOnSuccessListener { snap ->
                val userList = mutableListOf<UserDetailsModel>()

                for (userSnapshot in snap.children) {
                    val user = userSnapshot.getValue(UserDetailsModel::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                val contactsList = mutableListOf<ContactsModel>()
                for (user in userList) {
                    val contact = ContactsModel(user.name!!, user.email!!,user.uid!!)
                    contactsList.add(contact)
                }
                onResult(contactsList)
            }
            .addOnFailureListener { e ->
                    Log.e("Profile", "Failed to read user")
            }
    }

}