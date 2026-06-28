package com.novastream.android.transport

import android.util.Log
import com.novastream.android.protocol.Packet
import com.novastream.android.protocol.PacketWriter
import java.io.OutputStream
import java.net.Socket

class TcpTransport(
    private val host: String,
    private val port: Int
) : Transport {

    companion object {
        private const val TAG = "TcpTransport"
    }

    private var socket: Socket? = null
    private var output: OutputStream? = null

    override fun connect(): Boolean {

        return try {

            socket = Socket(host, port)

            socket?.tcpNoDelay = true

            output = socket?.getOutputStream()

            Log.i(TAG, "Connected to $host:$port")

            true

        } catch (e: Exception) {

            Log.e(TAG, "Connection failed", e)

            false
        }
    }

    override fun disconnect(): Boolean {

        return try {

            output?.close()
            socket?.close()

            output = null
            socket = null

            Log.i(TAG, "Disconnected")

            true

        } catch (e: Exception) {

            Log.e(TAG, "Disconnect failed", e)

            false
        }
    }

    override fun isConnected(): Boolean {

        return socket?.isConnected == true &&
                socket?.isClosed == false

    }

    override fun send(packet: Packet): Boolean {

        if (!isConnected()) {

            Log.w(TAG, "Transport is not connected")

            return false

        }

        return try {

            val bytes = PacketWriter.encode(packet)

            output?.write(bytes)

            output?.flush()

            true

        } catch (e: Exception) {

            Log.e(TAG, "Send failed", e)

            false
        }
    }
}