package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String,id: Long?,applicationContext: Context) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("id",id)
    val contentPendingIntent = PendingIntent.getActivity(applicationContext, id?.toInt()?:NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.unique_channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .addAction(R.drawable.ic_assistant_black_24dp,applicationContext.getString(R.string.notification_button),contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(id?.toInt()?:NOTIFICATION_ID, builder.build())  // create a different notification for every download
}

fun String.isValidUrl(): Boolean = URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()