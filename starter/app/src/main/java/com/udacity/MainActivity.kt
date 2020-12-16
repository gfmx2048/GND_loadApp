package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL_PROJECT = "https://github.com/square/retrofit/archive/master.zip"
        private const val URL_RETFORI = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GIDE = "https://github.com/bumptech/glide/archive/master.zip"
    }

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var mBinding: ActivityMainBinding

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification("Download complete $id", context)
            mBinding.content.customButton.setState(ButtonState.Completed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        mBinding.content.customButton.setOnClickListener {
            val checkedRb = mBinding.content.rgMain.checkedRadioButtonId
            if (checkedRb != -1) {
                download(when(checkedRb){
                    mBinding.content.rbRetrofit.id -> URL_RETFORI
                    mBinding.content.rbGlide.id -> URL_GIDE
                    else -> URL_PROJECT
                })
            } else {
                Toast.makeText(this,getString(R.string.please_select_a_repo), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun download(url :String) {
        val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}
