    package com.namangulati.studenthub.utils

    import android.util.Log
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.messaging.FirebaseMessagingService
    import com.google.firebase.messaging.RemoteMessage
    import com.namangulati.studenthub.API.FcmUtilits
    import com.namangulati.studenthub.userPages.MessagePage
    import com.namangulati.studenthub.utils.NotificationUtils.notificationDefaultPriority

    class PushNotificationService: FirebaseMessagingService() {
        override fun onNewToken(token: String) {
            super.onNewToken(token)

            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserUid != null) {
                val database = FirebaseDatabase.getInstance().getReference("userTokens")
                database.child(currentUserUid).setValue(token)
        }
        }

        override fun onMessageReceived(message: RemoteMessage) {
            super.onMessageReceived(message)
            val title = message.notification?.title ?: "New Notification"
            val body = message.notification?.body ?: "You have a new message"

            sendNotification(title, body)
        }

        private fun sendNotification(title: String, messageBody: String) {
            notificationDefaultPriority(MessagePage::class.java,this,title,messageBody)
        }
    }