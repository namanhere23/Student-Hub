package com.namangulati.studenthub.userPages

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.ContactsAdapter
import com.namangulati.studenthub.adapters.PapersAdapter
import com.namangulati.studenthub.models.ContactsModel
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadAllUsers

class Chat : AppCompatActivity() {
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactList:ArrayList<ContactsModel>
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val person = intent.getSerializableExtra("EXTRA_USER_DETAILS") as UserDetailsModel
        launchNavigationMenu(this,person)


        val progressBar=findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility= View.VISIBLE


        contactList = ArrayList()
        adapter = ContactsAdapter(this, contactList, person)


        contactRecyclerView = findViewById(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        contactRecyclerView.adapter = adapter

        loadAllUsers(this) { contactsList ->
            val list = mutableListOf<ContactsModel>()
            for(contact in contactsList){
                if(contact.email==person.email){
                    continue
                } else{
                    list.add(contact)
                }
            }
            contactList.clear()
            contactList.addAll(list)
            adapter.notifyDataSetChanged()
            progressBar.visibility=View.GONE
        }
    }
}