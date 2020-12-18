package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var mBinding: ActivityMainBinding

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification("Download complete $id", id, context)
            mBinding.content.customButton.setState(ButtonState.Completed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        mBinding.content.customButton.setOnClickListener {
            val checkedRb = mBinding.content.rgMain.checkedRadioButtonId
            if (checkedRb != -1) {
                download(
                    when (checkedRb) {
                        mBinding.content.rbRetrofit.id -> URL_RETFORI
                        mBinding.content.rbGlide.id -> URL_GIDE
                        mBinding.content.rbProject.id -> URL_PROJECT
                        else -> mBinding.content.etCustomUrl.text.toString()
                    }
                )
            } else {
                Toast.makeText(this, getString(R.string.please_select_a_repo), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun download(url: String) {
        if(!url.isValidUrl()) {
            Toast.makeText(this, getString(R.string.url_is_not_valid,url), Toast.LENGTH_SHORT).show()
            return
        }else{
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(url)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
