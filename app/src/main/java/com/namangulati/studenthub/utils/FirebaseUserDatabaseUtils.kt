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

    fun saveUser(context: Context, user: UserDetailsModel, onResult: (Boolean) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val newRef = if (user.uid != null) {
            usersRef.child(user.uid!!)
        } else {
            usersRef.push()
        }

        user.uid = newRef.key

        if (user.uid != null) {
            usersRef.child(user.uid!!).setValue(user)
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                    onResult(false)
                }

                .addOnSuccessListener {
                    onResult(true)
                }
        }
    }

    fun loadAllUsers(context: Context, onResult: (List<ContactsModel>) -> Unit) {
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
                    val contact = ContactsModel(user.name!!, user.email!!, user.uid!!)
                    contactsList.add(contact)
                }
                onResult(contactsList)
            }
            .addOnFailureListener { e ->
                Log.e("Profile", "Failed to read user")
            }
    }

    fun loadAllChats(context: Context, currentUserUid: String, onResult: (List<ContactsModel>) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseDatabase.getInstance()
        val usersChatsRef = database.getReference("usersChats").child(currentUserUid)
        val usersRef = database.getReference("users")

        usersChatsRef.get().addOnSuccessListener { chatPartnerSnapshots ->
            if (!chatPartnerSnapshots.exists()) {
                onResult(emptyList())
                return@addOnSuccessListener
            }

            val chatPartnerMap = mutableMapOf<String, Long>()
            for (snap in chatPartnerSnapshots.children) {
                val partnerUid = snap.key
                val time = snap.child("time").getValue(Long::class.java)
                if (partnerUid != null && time != null) {
                    chatPartnerMap[partnerUid] = time
                }
            }

            usersRef.get().addOnSuccessListener { allUsersSnapshot ->
                val chatContactsList = mutableListOf<ContactsModel>()

                val allUsersMap = mutableMapOf<String, UserDetailsModel>()
                for (userSnap in allUsersSnapshot.children) {
                    val user = userSnap.getValue(UserDetailsModel::class.java)
                    if (user != null && user.uid != null) {
                        allUsersMap[user.uid!!] = user
                    }
                }

                for ((partnerUid, time) in chatPartnerMap) {
                    val userDetails = allUsersMap[partnerUid]
                    if (userDetails != null) {
                        val contact = ContactsModel(
                            name = userDetails.name!!,
                            email = userDetails.email!!,
                            uid = userDetails.uid!!,
                            time = time
                        )
                        chatContactsList.add(contact)
                    }
                }
                onResult(chatContactsList)

            }.addOnFailureListener { e ->
                Log.e("FirebaseUser", "Failed to read all users", e)
                onResult(emptyList())
            }

        }.addOnFailureListener { e ->
            Log.e("FirebaseUser", "Failed to read usersChats", e)
            onResult(emptyList())
        }
    }

}