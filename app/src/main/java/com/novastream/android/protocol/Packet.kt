package com.novastream.android.protocol

data class Packet(

    val type: PacketType,

    val timestamp: Long,

    val payload: ByteArray

)