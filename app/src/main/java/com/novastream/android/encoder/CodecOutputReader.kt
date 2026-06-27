/*
 * NovaStream
 * Version 0.5
 *
 * File:
 * CodecOutputReader.kt
 */

package com.novastream.android.encoder

import android.media.MediaCodec
import android.media.MediaFormat
import kotlin.concurrent.thread

class CodecOutputReader(

    private val codec: MediaCodec,

    private val callback: EncoderCallback

) {

    @Volatile
    private var running = false

    private var readerThread: Thread? = null

    fun start() {

        if (running) {

            return

        }

        running = true

        readerThread = thread(start = true, name = "CodecOutputReader") {

            readLoop()

        }

    }

    fun stop() {

        running = false

        readerThread?.join(500)

        readerThread = null

    }

    private fun readLoop() {

        val bufferInfo = MediaCodec.BufferInfo()

        while (running) {

            val outputIndex = codec.dequeueOutputBuffer(

                bufferInfo,

                10000

            )

            when {

                outputIndex >= 0 -> {

                    val outputBuffer = codec.getOutputBuffer(outputIndex)

                    if (outputBuffer != null && bufferInfo.size > 0) {

                        outputBuffer.position(bufferInfo.offset)

                        outputBuffer.limit(

                            bufferInfo.offset + bufferInfo.size

                        )

                        val data = ByteArray(bufferInfo.size)

                        outputBuffer.get(data)

                        callback.onEncodedFrame(

                            codec,

                            outputBuffer,

                            bufferInfo

                        )

                    }

                    codec.releaseOutputBuffer(

                        outputIndex,

                        false

                    )

                }

                outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {

                    callback.onOutputFormatChanged(

                        codec.outputFormat

                    )

                }

            }

        }

    }

}