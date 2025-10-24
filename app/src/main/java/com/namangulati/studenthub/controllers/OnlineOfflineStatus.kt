package com.namangulati.studenthub.controllers

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OnlineOfflineStatus:Application(), LifecycleEventObserver {
    private var userStatusRef: DatabaseReference? = null
    override fun onCreate() {
        super.onCreate()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            userStatusRef = FirebaseDatabase.getInstance().getReference("users")
                .child(firebaseUser.uid)
                .child("status")
            userStatusRef?.onDisconnect()?.setValue("offline")
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                userStatusRef?.setValue("Online")
            }
            Lifecycle.Event.ON_STOP -> {
                userStatusRef?.setValue("Offline")
            }
            else -> {

            }
        }
    }
}