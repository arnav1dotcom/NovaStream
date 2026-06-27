package com.novastream.android.transport

import com.novastream.android.protocol.Packet

class UsbTransport : Transport {

    private var connected = false

    override fun connect(): Boolean {

        connected = true

        return connected

    }

    override fun disconnect(): Boolean {

        connected = false

        return true

    }

    override fun isConnected(): Boolean {

        return connected

    }

    override fun send(packet: Packet): Boolean {

        if (!connected) {

            return false

        }

        /*
         *
         * USB packet transmission
         * will be implemented here.
         *
         */

        return true

    }

}