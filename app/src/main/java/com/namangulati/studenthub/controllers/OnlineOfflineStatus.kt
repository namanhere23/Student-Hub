package com.namangulati.studenthub.controllers

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OnlineOfflineStatus : Application() {
    private var userStatusRef: DatabaseReference? = null
    private var presenceListener: LifecycleEventObserver? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun startPresenceListener() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        stopPresenceListener()

        userStatusRef = FirebaseDatabase.getInstance().getReference("users")
            .child(firebaseUser.uid)
            .child("status")

        presenceListener = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        userStatusRef?.setValue("Online")
                    }

                    Lifecycle.Event.ON_STOP -> {
                        userStatusRef?.setValue("Offline")
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        userStatusRef?.onDisconnect()?.setValue("offline")
        ProcessLifecycleOwner.get().lifecycle.addObserver(presenceListener!!)
    }

    fun stopPresenceListener() {
        presenceListener?.let {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(it)
            presenceListener = null
        }

        userStatusRef?.setValue("offline")
        userStatusRef?.onDisconnect()?.cancel()
        userStatusRef = null
    }


}