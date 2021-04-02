package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


private val NOTIFICATION_ID = 0

const val CHANNEL_ID = "channelId"
const val CHANNEL_NAME = "channelName"

fun NotificationManager.sendNotification(messageBody: String, appContext:Context, status:String){

    val intent = Intent(appContext, DetailActivity::class.java)
    intent.putExtra("fileName", messageBody)
    intent.putExtra("status", status)
    val pendingIntent = PendingIntent.getActivity(appContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .addAction(R.drawable.ic_launcher_foreground,appContext.getString(R.string.notification_button), pendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

fun cancelNotifications(context: Context) {
    val notificationManager = ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager
    notificationManager.cancelAll()
}