package com.example.aop_part3_chapter01

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val Tag = "FirebaseService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(Tag, "new Token: $token")

        //TODO: 서버에 토큰 갱신 처리 작업 해주어야함 라이브 서비스에서는
    }


    //TODO: 앱이 실행중이고, Firebase가 앱에 메시지를 보낼 떄 호출
    //TODO: Data메시지는 백그라운드, 포어그라운드에 상관없이 호출됨
   override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(Tag, "Notification Message Body: " + remoteMessage.notification?.body)
        Log.i(Tag, "Notification Message Title: " + remoteMessage.notification?.title)
        Log.i(Tag, "Notification Message id: " + remoteMessage.notification?.channelId)

        createNotificationChannel()

        val type = remoteMessage.data["type"]?.let {
            NotificationType.valueOf(it)
        }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this)
                .notify(type.id, createNotification(type, title, message))
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ):Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when(type) {
            NotificationType.NORMAL -> Unit //TODO: 할일 없음
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺️ 😊 😇 " +
                                    "🙂 🙃 😉 😌 😍 🥰 😘 😗 😙 😚 😋 😛 " +
                                    "😝 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩 🥳 😏 😒 " +
                                    "😞 😔 😟 😕 🙁 ☹ 😣 😖 😫 😩 🥺 😢 " +
                                    "😭 😤 😠 😡 🤬 🤯 😳 🥵 🥶 😱 😨 😰 " +
                                    "😥 😓 🤗 🤔 🤭 🤫 🤥 😶 😐 😑 😬 🙄 " +
                                    "😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 🥴 " +
                                    "🤢 🤮 🤧 😷 🤒 🤕"
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ). apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_ID = "Channel1"
        private const val CHANNEL_NAME = "My Test Channel"
    }

}