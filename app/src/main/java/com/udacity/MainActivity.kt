package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    private lateinit var linkUrl : String
    private lateinit var selectedUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener { download() }
    }

    private fun setButton(state: ButtonState) {
        custom_button.getButtonState(state)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_complete)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getUrl() : Boolean{
        var result = true
        when (url_radio_group.checkedRadioButtonId) {
            R.id.glide_radio_button -> {
                linkUrl = GLIDE_URL
                selectedUrl = getString(R.string.glide_radio_button)
            }
            R.id.load_app_radio_button -> {
                linkUrl = LOAD_APP_URL
                selectedUrl = getString(R.string.load_app_radio_button)
            }
            R.id.retrofit_radio_button -> {
                linkUrl = RETROFIT_URL
                selectedUrl = getString(R.string.retrofit_radio_button)
            }
            else -> result = false
        }
        return  result
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var statusDownload = getString(R.string.download_fail)

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val cursor:Cursor=downloadManager.query(DownloadManager.Query().setFilterById(id!!))

            if (cursor.moveToNext()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                cursor.close()
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    statusDownload = getString(R.string.download_success)
                }
            }
            notificationManager.sendNotification(selectedUrl, applicationContext, statusDownload)
            setButton(ButtonState.Completed)
        }
    }

    private fun download() {
        val checkUrl = getUrl()
        if (!checkUrl)
            return Toast.makeText(applicationContext, getString(R.string.select_file_download), Toast.LENGTH_SHORT).show()

        setButton(ButtonState.Loading)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(CHANNEL_ID, CHANNEL_NAME)

        val request =
                DownloadManager.Request(Uri.parse(linkUrl))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val LOAD_APP_URL=
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private val RETROFIT_URL=
            "https://github.com/square/retrofit"
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}
