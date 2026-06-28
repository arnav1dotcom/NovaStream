package com.novastream.android.protocol

object PacketHeader {

    const val MAGIC = 0x4E4F5641

    const val VIDEO = 1
    const val AUDIO = 2
    const val CONTROL = 3

    const val HEADER_SIZE = 24
}