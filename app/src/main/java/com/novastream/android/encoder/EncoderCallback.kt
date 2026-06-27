package com.novastream.android.encoder

import android.media.MediaCodec
import java.nio.ByteBuffer

interface EncoderCallback {

    fun onOutputFormatChanged(format: android.media.MediaFormat)

    fun onEncodedFrame(

        codec: MediaCodec,

        buffer: ByteBuffer,

        info: MediaCodec.BufferInfo

    )

}