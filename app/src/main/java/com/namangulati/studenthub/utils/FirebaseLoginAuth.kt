package com.namangulati.studenthub.utils

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.namangulati.studenthub.controllers.OnlineOfflineStatus
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.Dashboard
import com.namangulati.studenthub.userPages.Details_Page

object FirebaseLoginAuth {

    private fun firebaseAuthWithGoogle(activity: Activity, idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val uid = auth.uid
                    val name = user?.displayName
                    val email = user?.email
                    val phone = user?.phoneNumber
                    val photoUrl = user?.photoUrl?.toString()
                    var finalname= name
                    var finalEmail= email
                    var finalMobile=phone
                    var finalphoto=photoUrl

                    val iiitlPattern = Regex("^[A-Za-z0-9._%+-]+@iiitl\\.ac\\.in$", RegexOption.IGNORE_CASE)

                    if (!email?.let { iiitlPattern.matches(it) }!!) {
                        Toast.makeText(activity, "Please enter a valid IIITL email (e.g., name@iiitl.ac.in)", Toast.LENGTH_SHORT).show()
                    }

//                    if (false){}

                    else {
                        uid?.let { uid ->
                            FirebaseUserDatabaseUtils.loadUserByUid(activity, uid) { user ->
                                if (user != null) {
                                    finalname = (user.name ?: name).toString()
                                    finalEmail = user.email ?: email
                                    finalMobile = user?.mobile ?: phone
                                    finalphoto = (user.photo ?: photoUrl).toString()
                                }
                                val profile = UserDetailsModel(
                                    finalname,
                                    finalEmail,
                                    finalMobile,
                                    finalphoto,
                                    uid,
                                    user?.groups ?: arrayListOf()
                                )

                                if (profile != null) {
                                    FirebaseUserDatabaseUtils.saveUser(
                                        activity,
                                        profile
                                    ) { success ->
                                        (activity.application as OnlineOfflineStatus).startPresenceListener()
                                        val intent = if (finalMobile?.length != 10) {
                                            Intent(activity, Details_Page::class.java)
                                        } else {
                                            Intent(activity, Dashboard::class.java)
                                        }
                                        intent.putExtra("EXTRA_USER_DETAILS", profile)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        activity.startActivity(intent)
                                        activity.finish()
                                    }
                                }
                            }
                        }

                        Toast.makeText(activity, "Google sign-in successful!", Toast.LENGTH_SHORT)
                            .show()
                    }



                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        activity,
                        "Firebase authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signInUser(activity:Activity, email: String, password: String) {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    if (user.isEmailVerified) {
                        Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                        (activity.application as OnlineOfflineStatus).startPresenceListener()
                        val intent=Intent(activity,Details_Page::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    } else {
                        Toast.makeText(activity, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createNewUser(activity:Activity, email: String, password: String) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Verification email sent! Check your inbox.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener { e ->
                if (e.message?.contains("already in use") == true) {
                    signInUser(activity,email, password)
                } else {
                    Toast.makeText(activity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun checkIfUserExists(activity:Activity,email: String, password: String) {
        val auth = Firebase.auth
        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val signInMethods = result.signInMethods

                if (!signInMethods.isNullOrEmpty()) {
                    Toast.makeText(
                        activity,
                        "Account found. Enter password to sign in.",
                        Toast.LENGTH_SHORT
                    ).show()
                    signInUser(activity,email, password)
                } else {
                    createNewUser(activity,email,password)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Failed to check email: ${e.message}", Toast.LENGTH_LONG)
                    .show()
                Log.d("LoginDebug", "Failed to check email: ${e.message}")
            }
    }

    fun handleSignIn(activity:Activity,response: GetCredentialResponse) {
        val credential = response.credential
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(activity,googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }
}