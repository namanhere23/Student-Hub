package com.namangulati.studenthub.userPages

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.namangulati.studenthub.API.FcmUtilits
import com.namangulati.studenthub.R
import com.namangulati.studenthub.adapters.MessageAdapter
import com.namangulati.studenthub.models.Message
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadUserByUid
import com.namangulati.studenthub.utils.GetOfflineOnlineStatus.getOfflineOnlineStatus
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
        val mobile=intent.getStringExtra("mobile") as String
        launchNavigationMenu(this, person)
        senderRoom = ruid + person.uid
        receiverRoom = person.uid + ruid
        messageList = ArrayList()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = name

        val handler = Handler(Looper.getMainLooper())
        val interval = 5000L
        val runnable = object : Runnable {
            override fun run() {
                getOfflineOnlineStatus(this@MessagePage, ruid!!) { status ->
                    val color = if (status == "Online") "#00FF00" else "#FF0000"
                    supportActionBar?.subtitle = Html.fromHtml(
                        "<font color='$color'>$status</font>",
                        Html.FROM_HTML_MODE_LEGACY
                    )
                }
                handler.postDelayed(this, interval)
            }
        }

        handler.post(runnable)

        if (mobile != "0000000000") {
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setOnClickListener {
                Intent(this, ChatUserProfile::class.java).also {
                    it.putExtra("uid", ruid)
                    it.putExtra("EXTRA_USER_DETAILS", person)
                    startActivity(it)
                }
            }
        }


        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        SendMessagePic = findViewById(R.id.SendMessagePic)
        messageAdapter = MessageAdapter(this, messageList)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerMessages.layoutManager = layoutManager
        recyclerMessages.adapter = messageAdapter

        val database = FirebaseFirestore.getInstance()
        val chats = database.collection("chats")
        val usersChats = database.collection("usersChats")

        loadUserByUid(this, ruid!!) { user ->
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

                val timestampMs = Instant.now().toEpochMilli()
                val messageObject = Message(message, person.uid, timestampMs)

                loadUserByUid(this, ruid) {
                    if (it?.mobile.equals("0000000000")) {
                        chats.document(ruid).collection("messages").add(messageObject)
                            .addOnSuccessListener {
                                usersChats.document(ruid).collection("partners").document(person.uid!!).set(mapOf("time" to timestampMs))
                            }

                        etMessage.text.clear()

                    } else {
                        chats.document(senderRoom!!).collection("messages").add(messageObject)
                            .addOnSuccessListener {
                                chats.document(receiverRoom!!).collection("messages").add(messageObject)
                            }.addOnSuccessListener {
                                usersChats.document(person.uid!!).collection("partners").document(ruid).set(mapOf("time" to timestampMs))
                                usersChats.document(ruid).collection("partners").document(person.uid!!).set(mapOf("time" to timestampMs))
                            }

                        etMessage.text.clear()

                        val tokenRef = FirebaseFirestore.getInstance()
                            .collection("userTokens")
                            .document(ruid)

                        tokenRef.get().addOnSuccessListener { snapshot ->
                            val receiverToken = snapshot.getString("token")
                            if (!receiverToken.isNullOrEmpty()) {
                                FcmUtilits.sendMessageWithToken(receiverToken, message, person.name.toString())
                            }
                        }
                    }

                }

            }

        }
    }
    private fun setupChatListener(chats: CollectionReference, roomId: String) {
        chats.document(roomId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MessagePage", "Chat listener cancelled: ${error.message}")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    messageList.clear()
                    for (postSnapshot in snapshot.documents) {
                        val message = postSnapshot.toObject(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    if (messageList.isNotEmpty()) {
                        recyclerMessages.scrollToPosition(messageList.size - 1)
                    }
                }
            }
    }
}