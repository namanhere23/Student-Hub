package com.namangulati.studenthub.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.namangulati.studenthub.models.ContactsModel
import com.namangulati.studenthub.models.Groups
import com.namangulati.studenthub.models.UserDetailsModel
import java.time.Instant

object FirebaseUserDatabaseUtils {

    fun loadUserByUid(context: Context, uid: String, onResult: (UserDetailsModel?) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val usersRef = database.collection("users")
        usersRef.document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserDetailsModel::class.java)
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

        val database = FirebaseFirestore.getInstance()
        val usersRef = database.collection("users")
        val newRef = if (user.uid != null) {
            usersRef.document(user.uid!!)
        } else {
            usersRef.document()
        }

        user.uid = newRef.id

        if (user.uid != null) {
            usersRef.document(user.uid!!).set(user)
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to save : ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                    onResult(false)
                }
                .addOnSuccessListener {
                    onResult(true)
                }
        }
    }

    fun loadAllUsers(context: Context, currentUserUid: String,onResult: (List<ContactsModel>) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val usersRef = database.collection("users")

        usersRef.get()
            .addOnSuccessListener { snap ->
                val userList = mutableListOf<UserDetailsModel>()
                var userYear= ArrayList<String>()
                loadUserByUid(context,currentUserUid){
                    if (it != null) {
                        userYear= it.groups
                    }
                    for (userDocument in snap.documents) {
                        val user = userDocument.toObject(UserDetailsModel::class.java)

                        if (user != null) {
                            if(user.mobile!="0000000000")
                                userList.add(user)
                            else{
                                val accessYears=user.groups
                                for(year in userYear){
                                    if(accessYears.contains(year)){
                                        userList.add(user)
                                    }
                                }
                            }
                        }
                    }
                    val contactsList = mutableListOf<ContactsModel>()
                    for (user in userList) {
                        val contact =
                            user.name?.let { it1 -> user.email?.let { it2 ->
                                user.uid?.let { it3 ->
                                    user.mobile?.let { it4 ->
                                        user.photo?.let { it5 ->
                                            ContactsModel(it1,
                                                it2, it3, it4, it5
                                            )
                                        }
                                    }
                                }
                            } }
                        if (contact != null) {
                            contactsList.add(contact)
                        }
                    }
                    onResult(contactsList)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile", "Failed to read user")
            }
    }

    fun loadAllChats(
        context: Context,
        currentUserUid: String,
        onResult: (List<ContactsModel>) -> Unit
    ) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val usersChatsRef = database.collection("usersChats").document(currentUserUid).collection("partners")
        val usersRef = database.collection("users")
        val groupRef = database.collection("groups")

        usersChatsRef.get().addOnSuccessListener { chatPartnerSnapshots ->
            if (chatPartnerSnapshots.isEmpty) {
                onResult(emptyList())
                return@addOnSuccessListener
            }

            val chatPartnerMap = mutableMapOf<String, Long>()
            for (snap in chatPartnerSnapshots.documents) {
                val partnerUid = snap.id
                val time = snap.getLong("time")
                if (time != null) {
                    chatPartnerMap[partnerUid] = time
                }
            }

            var userYear= ArrayList<String>()

            loadUserByUid(context,currentUserUid){ currentUserObj ->
                if (currentUserObj != null) {
                    userYear= currentUserObj.groups
                }

                groupRef.get().addOnSuccessListener { groupQuerySnap ->
                    for (snap in groupQuerySnap.documents) {
                        val partnerUid = snap.id
                        val time = Instant.now().toEpochMilli()
                        val group = snap.toObject(Groups::class.java)
                        val accessYears = group?.accessYears
                        if (accessYears != null) {
                            for(year in accessYears){
                                if(userYear.contains(year)){
                                    chatPartnerMap[partnerUid] = time
                                }
                            }
                        }
                    }

                    usersRef.get().addOnSuccessListener { allUsersSnapshot ->
                        val chatContactsList = mutableListOf<ContactsModel>()

                        val allUsersMap = mutableMapOf<String, UserDetailsModel>()
                        for (userSnap in allUsersSnapshot.documents) {
                            val user = userSnap.toObject(UserDetailsModel::class.java)
                            if (user?.uid != null) {
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
                                    time = time,
                                    mobile = userDetails.mobile!!,
                                    url = userDetails.photo!!
                                )
                                chatContactsList.add(contact)
                            }
                        }
                        val sortedList = chatContactsList.sortedByDescending { it.time }
                        onResult(sortedList)
                    }


                }.addOnFailureListener { e ->
                    Log.e("FirebaseUser", "Failed to read all users", e)
                    onResult(emptyList())
                }


            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseUser", "Failed to read usersChats", e)
            onResult(emptyList())
        }
    }

    fun addGroups(context: Context, name: String, url: String,groups: ArrayList<String>, onResult: (Boolean) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val database = FirebaseFirestore.getInstance()
        val groupRef = database.collection("groups")
        val newGroupRef = groupRef.document()
        val uid = newGroupRef.id

        var group = Groups(uid, groups)
        groupRef.document(group.uid!!).set(group)
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed to save group: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onResult(false)
            }
            .addOnSuccessListener {
                saveUser(context, UserDetailsModel(name, "group$name@iiitl.ac.in", "0000000000", url, uid, groups, "group")
                ) {
                    onResult(true)
                }

            }
    }

    fun checkIfAdmin(context: Context, email: String, onResult: (Boolean) -> Unit) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        val database = FirebaseFirestore.getInstance()
        database.collection("admin").whereEqualTo("email", email).get()
            .addOnSuccessListener { snap ->
                onResult(!snap.isEmpty)
            }
            .addOnFailureListener { e ->
                Log.e("AdminCheck", "Failed to check admin access", e)
                onResult(false)
            }
    }
}