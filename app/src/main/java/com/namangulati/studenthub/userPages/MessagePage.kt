package com.namangulati.studenthub.userPages

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TimeUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.namangulati.studenthub.API.FcmApi
import com.namangulati.studenthub.API.FcmUtilits
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.MessageAdapter
import com.namangulati.studenthub.models.Message
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadUserByUid
import com.namangulati.studenthub.utils.GetOfflineOnlineStatus
import com.namangulati.studenthub.utils.GetOfflineOnlineStatus.getOfflineOnlineStatus
import retrofit2.Retrofit
import java.time.Instant

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
        launchNavigationMenu(this, person)
        senderRoom = ruid + person.uid
        receiverRoom = person.uid + ruid
        messageList = ArrayList()

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = name
        getOfflineOnlineStatus(this, ruid!!) { status ->
            val color = if (status == "Online") "#00FF00" else "#FF0000"
            supportActionBar?.subtitle = Html.fromHtml(
                "<font color='$color'>$status</font>",
                Html.FROM_HTML_MODE_LEGACY
            )
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
        val usersChats = database.getReference("usersChats")

        loadUserByUid(this, ruid) { user ->
            if (user == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                finish()
                return@loadUserByUid
            }

            if (user.mobile == "0000000000") {
                senderRoom = ruid
                receiverRoom = ruid
            } else {
                senderRoom = ruid + person.uid
                receiverRoom = person.uid + ruid
            }

            setupChatListener(chats, senderRoom!!)

            SendMessagePic.setOnClickListener {
                val message = etMessage.text.toString().trim()
                if (message.length == 0) {
                    return@setOnClickListener
                }

                val messageObject = Message(message, person.uid)

                loadUserByUid(this, ruid) {
                    if (it?.mobile.equals("0000000000")) {
                        chats.child(ruid).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                val time = Instant.now().toEpochMilli()
                                usersChats.child(ruid).child(person.uid!!).child("time")
                                    .setValue(time)
                            }

                        etMessage.text.clear()

                    } else {
                        chats.child(senderRoom!!).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                chats.child(receiverRoom!!).child("messages").push()
                                    .setValue(messageObject)
                            }.addOnSuccessListener {
                                val time = Instant.now().toEpochMilli()
                                usersChats.child(person.uid!!).child(ruid).child("time")
                                    .setValue(time)
                                usersChats.child(ruid).child(person.uid!!).child("time")
                                    .setValue(time)
                            }

                        etMessage.text.clear()

                        val tokenRef = FirebaseDatabase.getInstance()
                            .getReference("userTokens")
                            .child(ruid)

                        tokenRef.get().addOnSuccessListener { snapshot ->
                            val receiverToken = snapshot.getValue(String::class.java)
                            if (!receiverToken.isNullOrEmpty()) {
                                FcmUtilits.sendMessageWithToken(receiverToken, message)
                            }
                        }
                    }

                }

            }

        }
    }
    private fun setupChatListener(chats: DatabaseReference, roomId: String) {
        chats.child(roomId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    recyclerMessages.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MessagePage", "Chat listener cancelled: ${error.message}")
                }
            })
    }
}