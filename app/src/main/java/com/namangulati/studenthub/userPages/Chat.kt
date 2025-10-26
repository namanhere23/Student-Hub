package com.namangulati.studenthub.userPages

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.ChatsAdapter
import com.namangulati.studenthub.adapters.ContactsAdapter
import androidx.appcompat.widget.SearchView
import com.namangulati.studenthub.fragments.ProfileFragment
import com.namangulati.studenthub.models.ContactsModel
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.BottomNavigationLauncher.launchNavigationMenuBottom
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadAllChats
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadAllUsers
import java.util.Locale

class Chat : AppCompatActivity() {
    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactList: ArrayList<ContactsModel>
    private lateinit var displayedContactList: ArrayList<ContactsModel>
    private lateinit var adapter: ContactsAdapter
    private lateinit var adapter1: ChatsAdapter
    private lateinit var recyclerChatsView: RecyclerView
    private lateinit var chatList: ArrayList<ContactsModel>
    private lateinit var displayedChatList: ArrayList<ContactsModel>
    private lateinit var searchView: SearchView
    private lateinit var searchView1: SearchView

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
        launchNavigationMenu(this, person)
        launchNavigationMenuBottom(this,person)

        val panel = ProfileFragment().apply {
            arguments = Bundle().apply {
                putSerializable("EXTRA_USER_DETAILS", person)
            }
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.topPanel, panel)
            commit()
        }


        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        val progressBar1 = findViewById<ProgressBar>(R.id.progressBar1)
        progressBar1.visibility = View.VISIBLE


        contactList = ArrayList()
        displayedContactList = ArrayList()
        displayedChatList = ArrayList()
        adapter = ContactsAdapter(this, displayedContactList, person)

        contactRecyclerView = findViewById(R.id.recyclerContacts)
        contactRecyclerView.layoutManager = LinearLayoutManager(this)
        contactRecyclerView.adapter = adapter

        person.uid?.let {
            loadAllUsers(this, it) { contactsList ->
                val list = mutableListOf<ContactsModel>()
                for (contact in contactsList) {
                    if (contact.email == person.email) {
                        continue
                    } else {
                        list.add(contact)
                    }
                }
                contactList.clear()
                contactList.addAll(list)
                displayedContactList.clear()
                displayedContactList.addAll(contactList)
                adapter.notifyDataSetChanged()
                val params = contactRecyclerView.layoutParams
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                progressBar.visibility = View.GONE
            }
        }

        chatList = ArrayList()
        adapter1 = ChatsAdapter(this, displayedChatList, person)
        recyclerChatsView = findViewById(R.id.recyclerChats)
        recyclerChatsView.layoutManager = LinearLayoutManager(this)
        recyclerChatsView.adapter = adapter1

        loadAllChats(this, person.uid!!) { contactsList ->
            val sortedList = contactsList.sortedByDescending { it.time }
            chatList.clear()
            chatList.addAll(sortedList)
            displayedChatList.clear()
            displayedChatList.addAll(sortedList)

            adapter1.notifyDataSetChanged()

            val params = recyclerChatsView.layoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            progressBar1.visibility = View.GONE
        }

        searchView = findViewById(R.id.searchView)
        searchView.clearFocus()
        styleSearchView(searchView)

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

        searchView1 = findViewById(R.id.searchView1)
        searchView1.clearFocus()
        styleSearchView(searchView1)

        searchView1.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList1(newText)
                return true
            }

        })

        val pic=findViewById<TextView>(R.id.openContacts)
        pic.setOnClickListener{
            pic.visibility=View.GONE
            searchView.visibility=View.VISIBLE
            contactRecyclerView.visibility=View.VISIBLE
        }


    }

    private fun styleSearchView(searchView: SearchView) {
        val searchEditText =
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)
    }

    private fun filterList(query: String?) {
        var listToUpdate: List<ContactsModel>
        if (query.isNullOrEmpty()) {
            listToUpdate = contactList
        } else {
            listToUpdate = contactList.filter {
                it.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }
        }

        adapter.updateList(listToUpdate)
    }

    private fun filterList1(query: String?) {
        var listToUpdate: List<ContactsModel>
        if (query.isNullOrEmpty()) {
            listToUpdate = chatList
        } else {
            listToUpdate = chatList.filter {
                it.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
            }
        }

        adapter1.updateList(listToUpdate)
    }
}