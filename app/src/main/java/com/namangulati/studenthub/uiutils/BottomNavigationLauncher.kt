package com.namangulati.studenthub.uiutils

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.namangulati.studenthub.MainActivity
import com.namangulati.studenthub.R
import com.namangulati.studenthub.admin.AcceptOrRejectPapers
import com.namangulati.studenthub.controllers.OnlineOfflineStatus
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.Chat
import com.namangulati.studenthub.userPages.ChatBot
import com.namangulati.studenthub.userPages.Dashboard
import com.namangulati.studenthub.userPages.Details_Page

object BottomNavigationLauncher {
    fun launchNavigationMenuBottom(activity: Activity, person: UserDetailsModel) {
        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNavigationView?.let { nav ->
            val current = activity::class.java.simpleName
            when {
                current.contains("Dashboard", ignoreCase = true) -> nav.selectedItemId = R.id.nav_home
                current.contains("ChatBot", ignoreCase = true) -> nav.selectedItemId = R.id.chatBot
                current.contains("Chat", ignoreCase = true) -> nav.selectedItemId = R.id.nav_chat
                current.contains("AcceptOrRejectPapers", ignoreCase = true) -> nav.selectedItemId = R.id.admin
            }
            nav.setOnItemSelectedListener { item ->
                if (nav.selectedItemId == item.itemId) {
                    return@setOnItemSelectedListener true
                }

                when (item.itemId) {
                    R.id.nav_home -> {
                        val intent = Intent(activity, Dashboard::class.java)
                        intent.putExtra("EXTRA_USER_DETAILS", person)
                        activity.startActivity(intent)
                    }

                    R.id.nav_chat -> {
                        val intent = Intent(activity, Chat::class.java)
                        intent.putExtra("EXTRA_USER_DETAILS", person)
                        activity.startActivity(intent)
                    }

                    R.id.admin -> {
                        val intent = Intent(activity, AcceptOrRejectPapers::class.java)
                        intent.putExtra("EXTRA_USER_DETAILS", person)
                        activity.startActivity(intent)
                    }

                    R.id.chatBot -> {
                        val intent = Intent(activity, ChatBot::class.java)
                        intent.putExtra("EXTRA_USER_DETAILS", person)
                        activity.startActivity(intent)
                    }

                    R.id.nav_profile -> {
                        val intent = Intent(activity, Details_Page::class.java)
                        intent.putExtra("EXTRA_USER_DETAILS", person)
                        activity.startActivity(intent)
                    }
                }

                true
            }
        }

    }
}
