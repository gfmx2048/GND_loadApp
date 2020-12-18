package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import timber.log.Timber


class DetailActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDetailBinding
    private lateinit var mDownloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
        val toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)
        mDownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = intent.extras?.getLong("id")?:-1L
        cancelNotification(downloadId)
        initLayout(downloadId)
        statusOfDownloadManager(downloadId)
    }

    private fun cancelNotification(downloadId: Long) {
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        if(downloadId!=-1L){
            notificationManager.cancel(downloadId.toInt())
        }else{
            notificationManager.cancelAll()
        }
    }

    private fun initLayout(downloadId: Long) {
        if(downloadId!=-1L){
            val uri = mDownloadManager.getUriForDownloadedFile(downloadId)
            val mimeType: String? = mDownloadManager.getMimeTypeForDownloadedFile(downloadId)

            mBinding.content.fileName.text = uri?.path
            mBinding.content.btViewFile.visibility = View.VISIBLE
            mBinding.content.btViewFile.setOnClickListener {
                try {
                    val fileIntent = Intent(Intent.ACTION_VIEW)
                    fileIntent.setDataAndType(uri, mimeType)
                    fileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(fileIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, getString(R.string.no_app_to_display), Toast.LENGTH_LONG).show()
                }
            }

        }else{
            mBinding.content.status.text = getString(R.string.failed)
            mBinding.content.btViewFile.visibility = View.GONE
        }

        mBinding.content.btOk.setOnClickListener {
            finish()
        }
    }

    private fun statusOfDownloadManager(downloadId: Long){
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = mDownloadManager.query(query)
        if (cursor.moveToFirst()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_PAUSED -> {
                    Timber.d("PAUSED")
                }
                DownloadManager.STATUS_PENDING -> {
                    Timber.d("PENDING")
                }
                DownloadManager.STATUS_RUNNING -> {
                    Timber.d("RUNNING")
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    mBinding.content.fileName.text = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    mBinding.content.status.text = getString(R.string.success)
                }
                DownloadManager.STATUS_FAILED -> {
                    mBinding.content.fileName.text = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))?:getString(
                                            R.string.error_placeholder)
                    mBinding.content.status.text = getString(R.string.failed)
                    mBinding.content.status.setTextColor(ContextCompat.getColor(this,R.color.colorRed))
                }
            }
        }
    }

}
