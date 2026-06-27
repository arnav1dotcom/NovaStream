package com.novastream.android.protocol

class Packetizer {

    fun createVideoPacket(

        data: ByteArray,

        timestamp: Long

    ): Packet {

        return Packet(

            type = PacketType.VIDEO,

            timestamp = timestamp,

            payload = data

        )

    }

    fun createAudioPacket(

        data: ByteArray,

        timestamp: Long

    ): Packet {

        return Packet(

            type = PacketType.AUDIO,

            timestamp = timestamp,

            payload = data

        )

    }

    fun createControlPacket(

        data: ByteArray

    ): Packet {

        return Packet(

            type = PacketType.CONTROL,

            timestamp = System.nanoTime(),

            payload = data

        )

    }

    fun createConfigPacket(

        data: ByteArray

    ): Packet {

        return Packet(

            type = PacketType.CONFIG,

            timestamp = System.nanoTime(),

            payload = data

        )

    }

}