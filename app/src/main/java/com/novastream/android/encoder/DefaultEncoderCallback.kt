package com.novastream.android.encoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.novastream.android.protocol.Packet
import com.novastream.android.protocol.Packetizer
import com.novastream.android.transport.Transport
import java.nio.ByteBuffer

class DefaultEncoderCallback(

    private val packetizer: Packetizer,

    private val transport: Transport

) : EncoderCallback {

    companion object {

        private const val TAG = "NovaStreamEncoder"

    }

    private var outputFormat: MediaFormat? = null

    private var frameCount = 0L

    override fun onOutputFormatChanged(
        format: MediaFormat
    ) {

        outputFormat = format

        Log.d(
            TAG,
            "Output format changed."
        )

    }

    override fun onEncodedFrame(

        codec: MediaCodec,

        buffer: ByteBuffer,

        info: MediaCodec.BufferInfo

    ) {

        if (info.size <= 0) {

            return

        }

        val bytes = ByteArray(info.size)

        buffer.position(info.offset)
        buffer.limit(info.offset + info.size)

        buffer.get(bytes)

        val isKeyFrame =
            (info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0

        frameCount++

        val packet: Packet =
            packetizer.createVideoPacket(
                bytes,
                info.presentationTimeUs
            )

        transport.send(packet)

        Log.d(
            TAG,
            "Frame $frameCount | ${bytes.size} bytes | KeyFrame=$isKeyFrame"
        )
    }
}