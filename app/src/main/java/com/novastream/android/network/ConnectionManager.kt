package com.novastream.android.network

import com.novastream.android.model.ConnectionType

class ConnectionManager {

    private var connectionType =
        ConnectionType.USB

    fun setConnectionType(
        type: ConnectionType
    ) {
        connectionType = type
    }

    fun getConnectionType(): ConnectionType {
        return connectionType
    }

    fun isUsb(): Boolean {
        return connectionType == ConnectionType.USB
    }

    fun isWifi(): Boolean {
        return connectionType == ConnectionType.WIFI
    }
}