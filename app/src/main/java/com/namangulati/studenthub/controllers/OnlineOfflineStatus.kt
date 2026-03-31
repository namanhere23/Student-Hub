package com.namangulati.studenthub.controllers

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class OnlineOfflineStatus : Application() {
    private var userStatusRef: DocumentReference? = null
    private var presenceListener: LifecycleEventObserver? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun startPresenceListener() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        stopPresenceListener()

        userStatusRef = FirebaseFirestore.getInstance().collection("users")
            .document(firebaseUser.uid)

        presenceListener = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        userStatusRef?.update(
                            "status", "Online",
                            "lastSeen", System.currentTimeMillis()
                        )
                    }

                    Lifecycle.Event.ON_STOP -> {
                        userStatusRef?.update(
                            "status", "Offline",
                            "lastSeen", System.currentTimeMillis()
                        )
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(presenceListener!!)
    }

    fun stopPresenceListener() {
        presenceListener?.let {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(it)
            presenceListener = null
        }

        userStatusRef?.update(
            "status", "Offline",
            "lastSeen", System.currentTimeMillis()
        )
        userStatusRef = null
    }


}