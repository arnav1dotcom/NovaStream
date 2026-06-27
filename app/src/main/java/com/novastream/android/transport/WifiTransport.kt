package com.novastream.android.transport

import com.novastream.android.protocol.Packet

class WifiTransport : Transport {

    override fun connect(): Boolean {

        // WiFi connection will be implemented later.

        return true
    }

    override fun disconnect(): Boolean {

        // WiFi disconnection will be implemented later.

        return true
    }

    override fun send(packet: Packet): Boolean {

        // Packet sending will be implemented later.

        return true
    }

    override fun isConnected(): Boolean {

        return false
    }
}