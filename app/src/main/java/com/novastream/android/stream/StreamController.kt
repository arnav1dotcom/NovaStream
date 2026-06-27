package com.novastream.android.stream

import android.view.Surface
import com.novastream.android.capture.CaptureSession
import com.novastream.android.encoder.VideoEncoder
import com.novastream.android.model.CaptureConfig
import com.novastream.android.protocol.Packetizer
import com.novastream.android.transport.Transport

class StreamController(

    private val captureSession: CaptureSession,

    private val videoEncoder: VideoEncoder,

    private val packetizer: Packetizer,

    private val transport: Transport,

    private val config: CaptureConfig

) {

    private var streaming = false

    fun startStream() {

        if (streaming) return

        if (!transport.connect()) {
            return
        }

        videoEncoder.initialize(

            config.width,

            config.height,

            config.fps,

            config.bitrate

        )

        val surface = videoEncoder.getInputSurface()

        if (surface == null) {

            transport.disconnect()

            videoEncoder.release()

            return

        }

        captureSession.start(surface)

        videoEncoder.start()

        streaming = true

    }

    fun stopStream() {

        if (!streaming) return

        captureSession.stop()

        videoEncoder.stop()

        videoEncoder.release()

        transport.disconnect()

        streaming = false

    }

    fun isStreaming(): Boolean {

        return streaming

    }

}