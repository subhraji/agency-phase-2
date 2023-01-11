package com.example.agencyphase2.utils

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.ui.activity.AskLocationActivity
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("message","message recieved..."+message.notification?.title)
        if(message.notification != null){
            getNotification(message.notification?.title.toString(), message.notification?.body.toString())
        }
    }

    private fun getRemoveView(title: String?, body: String?): RemoteViews? {
        val remoteview = RemoteViews("com.example.agencyphase2",com.example.agencyphase2.R.layout.notification)
        remoteview.setTextViewText(com.example.agencyphase2.R.id.noti_title_tv,title)
        remoteview.setTextViewText(com.example.agencyphase2.R.id.desc_tv,body)
        remoteview.setImageViewResource(com.example.agencyphase2.R.id.notification_img,com.example.agencyphase2.R.drawable.ic_baseline_circle_notifications_24)

        return remoteview
    }

    private fun getNotification(title: String?, body: String?){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val channelId = "Default"
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_clear_all)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)

        builder = builder.setContent(getRemoveView(title,body))

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("token","new token")
    }

}