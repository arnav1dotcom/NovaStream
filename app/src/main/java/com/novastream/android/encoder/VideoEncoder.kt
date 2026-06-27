/*
 * NovaStream
 * Version 0.5
 *
 * File:
 * VideoEncoder.kt
 */

package com.novastream.android.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.view.Surface

class VideoEncoder(

    private val callback: EncoderCallback

) {

    companion object {

        private const val MIME_TYPE = "video/avc"

        private const val DEFAULT_IFRAME_INTERVAL = 1

    }

    private var mediaCodec: MediaCodec? = null

    private var inputSurface: Surface? = null

    private var outputReader: CodecOutputReader? = null

    fun initialize(

        width: Int,

        height: Int,

        fps: Int,

        bitrate: Int

    ) {

        mediaCodec = MediaCodec.createEncoderByType(

            MIME_TYPE

        )

        val format = MediaFormat.createVideoFormat(

            MIME_TYPE,

            width,

            height

        )

        format.setInteger(

            MediaFormat.KEY_COLOR_FORMAT,

            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface

        )

        format.setInteger(

            MediaFormat.KEY_BIT_RATE,

            bitrate

        )

        format.setInteger(

            MediaFormat.KEY_FRAME_RATE,

            fps

        )

        format.setInteger(

            MediaFormat.KEY_I_FRAME_INTERVAL,

            DEFAULT_IFRAME_INTERVAL

        )

        mediaCodec!!.configure(

            format,

            null,

            null,

            MediaCodec.CONFIGURE_FLAG_ENCODE

        )

        inputSurface = mediaCodec!!.createInputSurface()

        outputReader = CodecOutputReader(

            mediaCodec!!,

            callback

        )

    }

    fun start() {

        mediaCodec?.start()

        outputReader?.start()

    }

    fun stop() {

        outputReader?.stop()

        try {

            mediaCodec?.stop()

        } catch (_: Exception) {
        }

    }

    fun release() {

        stop()

        inputSurface?.release()

        mediaCodec?.release()

        outputReader = null

        inputSurface = null

        mediaCodec = null

    }

    fun getInputSurface(): Surface? {

        return inputSurface

    }

    fun getMediaCodec(): MediaCodec? {

        return mediaCodec

    }

}