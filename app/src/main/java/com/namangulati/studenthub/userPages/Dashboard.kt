package com.namangulati.studenthub.userPages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.namangulati.studenthub.MainActivity
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.PapersAdapter
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils

class Dashboard : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var toolbar: MaterialToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        val person = intent.getSerializableExtra("EXTRA_USER_DETAILS") as UserDetailsModel

        navigationView?.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Intent(this,Dashboard::class.java).also {
                        it.putExtra("EXTRA_USER_DETAILS", person)
                        startActivity(it)
                    }
                }
                R.id.nav_dashboard -> {
                    Intent(this,Dashboard::class.java).also {
                        it.putExtra("EXTRA_USER_DETAILS", person)
                        startActivity(it)
                    }
                }
                R.id.admin -> {
                    Intent(this,Dashboard::class.java).also {
                        it.putExtra("EXTRA_USER_DETAILS", person)
                        startActivity(it)
                    }
                }
                R.id.nav_logout -> {
                    if (FirebaseApp.getApps(this).isEmpty()) {
                        FirebaseApp.initializeApp(this)
                    }
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            drawerLayout?.closeDrawers()
            true
        }

        val headerView = navigationView?.getHeaderView(0)
        val headerProfilePic = headerView?.findViewById<ImageView>(R.id.headerProfilePic)
        val headerUserName = headerView?.findViewById<TextView>(R.id.headerUserName)
        if (!person.uid.isNullOrEmpty()) {

            FirebaseUserDatabaseUtils.loadUserByUid(this,person.uid!!) { user ->
                if (user != null) {

                    headerUserName?.text = user.name ?: "Welcome User"
                    if (!user.photo.isNullOrEmpty()) {
                        val imageUrl = user.photo?.replace("http://", "https://")
                        if (headerProfilePic != null) {
                            Glide.with(this)
                                .load(imageUrl)
                                .into(headerProfilePic)
                        }

                    } else {
                        headerProfilePic?.setImageResource(R.drawable.ic_profile_pic)
                    }
                }
            }
        }


        FirebasePapersDatabaseUtils.loadAllPapers(this) { papers ->
            val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerOrders)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = PapersAdapter(this, papers)
            recyclerView.adapter = adapter
        }


    }


}