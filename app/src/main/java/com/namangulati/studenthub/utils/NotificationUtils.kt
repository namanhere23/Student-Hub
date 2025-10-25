package com.namangulati.studenthub.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.namangulati.studenthub.R

object NotificationUtils {
    const val CHANNEL_ID = "Channel"
    const val CHANNEL_NAME = "ChannelName"

    private fun createPendingIntent(context: Context, activity: Class<out Activity>): PendingIntent {
        createNotificationChannel(context)
        val intents= Intent(context,activity)
         val pendingIntent = TaskStackBuilder.create(context).run {
             addNextIntentWithParentStack(intents)
             getPendingIntent(
                 0,
                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
             )
         }

        return pendingIntent
    }

    fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel=
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    lightColor= Color.GREEN
                    enableLights(true)
                }

            val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun notificationHighPriority(activity: Class<out Activity>,context:Context,title:String,text:String){
        val pendingIntent=createPendingIntent(context,activity)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager= NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(0,notification)
    }

    fun notificationDefaultPriority(activity: Class<out Activity>,context:Context,title:String,text:String){
        val pendingIntent=createPendingIntent(context,activity)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager= NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(0,notification)
    }

    fun notificationLowPriority(activity: Class<out Activity>,context:Context,title:String,text:String){
        val pendingIntent=createPendingIntent(context,activity)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager= NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { return }
        notificationManager.notify(0,notification)
    }
}