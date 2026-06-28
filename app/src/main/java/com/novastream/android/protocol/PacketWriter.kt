package com.novastream.android.protocol

import java.nio.ByteBuffer
import java.nio.ByteOrder

object PacketWriter {

    fun encode(packet: Packet): ByteArray {

        val buffer = ByteBuffer.allocate(
            PacketHeader.HEADER_SIZE + packet.payload.size
        )

        buffer.order(ByteOrder.BIG_ENDIAN)

        buffer.putInt(PacketHeader.MAGIC)

        buffer.putInt(packet.type)

        buffer.putLong(packet.timestamp)

        buffer.putInt(packet.flags)

        buffer.putInt(packet.payload.size)

        buffer.put(packet.payload)

        return buffer.array()
    }
}