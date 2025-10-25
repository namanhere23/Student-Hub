package com.namangulati.studenthub.uiutils

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.window.application
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.namangulati.studenthub.MainActivity
import com.namangulati.studenthub.R
import com.namangulati.studenthub.admin.AcceptOrRejectPapers
import com.namangulati.studenthub.controllers.OnlineOfflineStatus
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.Chat
import com.namangulati.studenthub.userPages.Dashboard
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils

object NavigationMenuLauncher {

    fun launchNavigationMenu(activity: Activity, person: UserDetailsModel){
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = activity.findViewById<NavigationView>(R.id.navigation_view)

        navigationView?.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent=Intent(activity, Dashboard::class.java)
                    intent.putExtra("EXTRA_USER_DETAILS", person)
                    activity.startActivity(intent)
                }
                R.id.nav_chat -> {
                    val intent=Intent(activity, Chat::class.java)
                    intent.putExtra("EXTRA_USER_DETAILS", person)
                    activity.startActivity(intent)
                }
                R.id.admin -> {
                    val intent=Intent(activity, AcceptOrRejectPapers::class.java)
                    intent.putExtra("EXTRA_USER_DETAILS", person)
                    activity.startActivity(intent)
                }
                R.id.nav_logout -> {
                    if (FirebaseApp.getApps(activity).isEmpty()) {
                        FirebaseApp.initializeApp(activity)
                    }
                    (activity.application as OnlineOfflineStatus).stopPresenceListener()
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    activity.startActivity(intent)
                }
            }
            drawerLayout?.closeDrawers()
            true
        }

        val headerView = navigationView?.getHeaderView(0)
        val headerProfilePic = headerView?.findViewById<ImageView>(R.id.headerProfilePic)
        val headerUserName = headerView?.findViewById<TextView>(R.id.headerUserName)
        if (!person.uid.isNullOrEmpty()) {

            FirebaseUserDatabaseUtils.loadUserByUid(activity,person.uid!!) { user ->
                if (user != null) {

                    headerUserName?.text = user.name ?: "Welcome User"
                    if (!user.photo.isNullOrEmpty()) {
                        val imageUrl = user.photo?.replace("http://", "https://")
                        if (headerProfilePic != null) {
                            Glide.with(activity)
                                .load(imageUrl)
                                .into(headerProfilePic)
                        }

                    } else {
                        headerProfilePic?.setImageResource(R.drawable.ic_profile_pic)
                    }
                }
            }
        }

    }
}