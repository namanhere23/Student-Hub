package com.namangulati.studenthub.userPages

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.namangulati.studenthub.LiveDataViewModel
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.PapersAdapter
import com.namangulati.studenthub.fragments.UploadFragment
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val person = intent.getSerializableExtra("EXTRA_USER_DETAILS") as UserDetailsModel
        NavigationMenuLauncher.launchNavigationMenu(this,person)

        val upload= UploadFragment()
        var state=0

        val uploadPhoto=findViewById<ImageView>(R.id.UploadPhoto)
        val viewModel = ViewModelProvider(this).get(LiveDataViewModel::class.java)
        viewModel.setUser(person)
        uploadPhoto.setOnClickListener {
            if(state%2==0) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.Upload, upload)
                    commit()
                }
                state=1
            } else {
                supportFragmentManager.beginTransaction().apply {
                    remove(upload)
                    commit()
                }
                state=0
            }

        }
        FirebasePapersDatabaseUtils.loadAllPapers(this) { papers ->
            val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerOrders)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = PapersAdapter(this, papers)
            val progressBar=findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility=View.GONE
            recyclerView.adapter = adapter
        }
    }
}