package com.novastream.android.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.novastream.android.R

class CaptureService : Service() {

    companion object {

        const val ACTION_START =
            "com.novastream.android.action.START_CAPTURE"

        const val ACTION_STOP =
            "com.novastream.android.action.STOP_CAPTURE"

        const val EXTRA_RESULT_CODE =
            "extra_result_code"

        const val EXTRA_DATA =
            "extra_data"

        private const val CHANNEL_ID =
            "NovaStreamCapture"

        private const val CHANNEL_NAME =
            "NovaStream Capture"

        private const val NOTIFICATION_ID = 1001
    }

    private var mediaProjection: MediaProjection? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startForeground(
            NOTIFICATION_ID,
            createNotification("Waiting for permission...")
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> {

                val resultCode = intent.getIntExtra(
                    EXTRA_RESULT_CODE,
                    Activity.RESULT_CANCELED
                )

                val data = intent.getParcelableExtra<Intent>(
                    EXTRA_DATA
                )

                if (data != null) {

                    val projectionManager =
                        getSystemService(
                            Context.MEDIA_PROJECTION_SERVICE
                        ) as MediaProjectionManager

                    mediaProjection =
                        projectionManager.getMediaProjection(
                            resultCode,
                            data
                        )

                    updateNotification(
                        "MediaProjection Connected"
                    )

                    /*
                     * Next version:
                     * CaptureSession
                     * VideoEncoder
                     * StreamController
                     */

                }
            }

            ACTION_STOP -> {
                stopCapture()
            }
        }

        return START_STICKY
    }

    private fun stopCapture() {

        mediaProjection?.stop()
        mediaProjection = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {

        stopCapture()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description = "NovaStream Capture Service"

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("NovaStream")
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(text: String) {

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.notify(
            NOTIFICATION_ID,
            createNotification(text)
        )
    }
}