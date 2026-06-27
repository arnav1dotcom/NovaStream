package com.novastream.android.transport

import com.novastream.android.protocol.Packet

interface Transport {

    fun connect(): Boolean

    fun disconnect(): Boolean

    fun isConnected(): Boolean

    fun send(packet: Packet): Boolean

}