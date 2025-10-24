package com.namangulati.studenthub.userPages

import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.MessageAdapter
import com.namangulati.studenthub.models.Message
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.GetOfflineOnlineStatus
import com.namangulati.studenthub.utils.GetOfflineOnlineStatus.getOfflineOnlineStatus
import java.time.Instant
import java.time.LocalDateTime

class MessagePage : AppCompatActivity() {
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var SendMessagePic: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_message_page)
        val name = intent.getStringExtra("contact")
        val ruid = intent.getStringExtra("uid")
        val person = intent.getSerializableExtra("EXTRA_USER_DETAILS") as UserDetailsModel
        launchNavigationMenu(this,person)
        senderRoom = ruid + person.uid
        receiverRoom = person.uid + ruid

        Log.d("Hello11", senderRoom!!)

        messageList = ArrayList()

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = name
        getOfflineOnlineStatus(this, ruid!!) { status ->
            supportActionBar?.subtitle = status ?: "..."
        }

        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        SendMessagePic = findViewById(R.id.SendMessagePic)
        messageAdapter = MessageAdapter(this, messageList)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerMessages.layoutManager = layoutManager
        recyclerMessages.adapter = messageAdapter

        val database = FirebaseDatabase.getInstance()
        val chats = database.getReference("chats")
        val usersChats=database.getReference("usersChats")

        SendMessagePic.setOnClickListener {
            val message = etMessage.text.toString()
            val messageObject = Message(message, person.uid)
            chats.child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    chats.child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                } .addOnSuccessListener {
                    val time=Instant.now().toEpochMilli()
                    usersChats.child(person.uid!!).child(ruid!!).child("time").setValue(time)
                    usersChats.child(ruid).child(person.uid!!).child("time").setValue(time)
                }
            etMessage.text.clear()
        }



        chats.child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}