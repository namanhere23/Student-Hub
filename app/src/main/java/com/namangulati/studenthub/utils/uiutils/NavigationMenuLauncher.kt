package com.namangulati.studenthub.uiutils

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.namangulati.studenthub.userPages.ChatBot
import com.namangulati.studenthub.userPages.Dashboard
import com.namangulati.studenthub.userPages.Details_Page
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
                    val email = person.email ?: ""
                    if (email.isNotEmpty()) {
                        FirebaseUserDatabaseUtils.checkIfAdmin(activity, email) { isAdmin ->
                            if (isAdmin) {
                                val intent = Intent(activity, AcceptOrRejectPapers::class.java)
                                intent.putExtra("EXTRA_USER_DETAILS", person)
                                activity.startActivity(intent)
                            } else {
                                Toast.makeText(activity, "Access Denied: You do not have admin privileges.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(activity, "Error: User email not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.nav_profile -> {
                    val intent = Intent(activity, Details_Page::class.java)
                    intent.putExtra("EXTRA_USER_DETAILS", person)
                    activity.startActivity(intent)
                }
                R.id.chatBot->{
                    val intent=Intent(activity, ChatBot::class.java)
                    intent.putExtra("EXTRA_USER_DETAILS", person)
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
                                .circleCrop()
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