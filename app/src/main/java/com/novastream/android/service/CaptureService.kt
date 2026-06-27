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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.novastream.android.R
import com.novastream.android.capture.CaptureSession
import com.novastream.android.encoder.VideoEncoder
import com.novastream.android.encoder.DefaultEncoderCallback
import com.novastream.android.model.CaptureConfig
import com.novastream.android.protocol.Packetizer
import com.novastream.android.transport.UsbTransport
import com.novastream.android.stream.StreamController

class CaptureService : Service() {

    companion object {
        const val ACTION_START = "com.novastream.android.action.START_CAPTURE"
        const val ACTION_STOP = "com.novastream.android.action.STOP_CAPTURE"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_DATA = "extra_data"

        private const val CHANNEL_ID = "NovaStreamCapture"
        private const val CHANNEL_NAME = "NovaStream Capture"
        private const val NOTIFICATION_ID = 1001

        private const val TAG = "CaptureService"
    }

    private var mediaProjection: MediaProjection? = null
    private var captureSession: CaptureSession? = null
    private var streamController: StreamController? = null

    // Pipeline components (created in correct order)
    private var packetizer: Packetizer? = null
    private var transport: UsbTransport? = null
    private var encoderCallback: DefaultEncoderCallback? = null
    private var videoEncoder: VideoEncoder? = null
    private var captureConfig: CaptureConfig? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Waiting for permission..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Prevent duplicate starts
                if (mediaProjection != null) {
                    Log.w(TAG, "Capture already running; ignoring duplicate start")
                    return START_NOT_STICKY
                }

                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                val data = intent.getParcelableExtra<Intent>(EXTRA_DATA)
                if (data == null) {
                    Log.e(TAG, "Missing MediaProjection data Intent")
                    updateNotification("Missing permission data")
                    return START_NOT_STICKY
                }

                val projectionManager =
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

                val projection = projectionManager.getMediaProjection(resultCode, data)
                if (projection == null) {
                    Log.e(TAG, "getMediaProjection returned null")
                    updateNotification("Failed to obtain MediaProjection")
                    return START_NOT_STICKY
                }

                mediaProjection = projection

                // Safe callback: stop the service; let onDestroy handle cleanup
                mediaProjection?.registerCallback(object : MediaProjection.Callback() {
                    override fun onStop() {
                        Log.i(TAG, "MediaProjection stopped by system/user; stopping service")
                        stopSelf()
                    }
                }, null)

                updateNotification("MediaProjection Connected")

                // Create CaptureSession
                captureSession = try {
                    CaptureSession(this, projection)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create CaptureSession", e)
                    updateNotification("Capture session failed")
                    projection.stop()
                    mediaProjection = null
                    return START_NOT_STICKY
                }

                // --- Create pipeline in the exact order your code requires (cleaner version) ---
                try {
                    val session = captureSession ?: return START_NOT_STICKY

                    packetizer = Packetizer()

                    transport = UsbTransport()

                    encoderCallback = DefaultEncoderCallback(
                        packetizer!!,
                        transport!!
                    )

                    val callback = encoderCallback
                        ?: throw IllegalStateException("EncoderCallback is null")

                    videoEncoder = VideoEncoder(callback)

                    captureConfig = CaptureConfig(
                        width = session.getWidth(),
                        height = session.getHeight(),
                        fps = 120,
                        bitrate = 40_000_000
                    )

                    val encoder = videoEncoder
                        ?: throw IllegalStateException("VideoEncoder is null")

                    val packetizerInstance = packetizer
                        ?: throw IllegalStateException("Packetizer is null")

                    val transportInstance = transport
                        ?: throw IllegalStateException("Transport is null")

                    val config = captureConfig
                        ?: throw IllegalStateException("CaptureConfig is null")

                    streamController = StreamController(
                        session,
                        encoder,
                        packetizerInstance,
                        transportInstance,
                        config
                    )

                    // Start streaming (StreamController will initialize encoder)
                    streamController?.startStream()

                    updateNotification(
                        "Streaming: ${config.width}×${config.height} @ ${config.fps} FPS"
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize streaming pipeline", e)
                    updateNotification("Stream start failed")
                    // Cleanup partial state
                    try { captureSession?.stop() } catch (ex: Exception) { /* ignore */ }
                    captureSession = null
                    try { mediaProjection?.stop() } catch (ex: Exception) { /* ignore */ }
                    mediaProjection = null
                    try { videoEncoder?.release() } catch (ex: Exception) { /* ignore */ }
                    videoEncoder = null
                    encoderCallback = null
                    packetizer = null
                    try {
                        transport?.disconnect()
                    } catch (ex: Exception) {
                        Log.w(TAG, "Error disconnecting transport", ex)
                    }
                    transport = null
                    captureConfig = null
                    return START_NOT_STICKY
                }
            }

            ACTION_STOP -> {
                stopCapture()
            }

            else -> {
                Log.w(TAG, "Unknown action or null intent")
            }
        }

        return START_NOT_STICKY
    }

    private fun stopCapture() {

        if (
            streamController == null &&
            captureSession == null &&
            mediaProjection == null
        ) {
            return
        }

        try {
            streamController?.stopStream()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping stream", e)
        }

        streamController = null

        try {
            captureSession?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping capture session", e)
        }

        captureSession = null

        try {
            mediaProjection?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping MediaProjection", e)
        }

        mediaProjection = null

        packetizer = null
        transport = null
        encoderCallback = null
        videoEncoder = null
        captureConfig = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

    override fun onDestroy() {
        try {
            stopCapture()
        } catch (e: Exception) {
            Log.w(TAG, "Error during service cleanup", e)
        }
        super.onDestroy()
    }

    // Implement onBind with the correct signature
    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "NovaStream Capture Service" }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("NovaStream")
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(text))
    }
}
