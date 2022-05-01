package xyz.a00000.gamewirelesscontroller.conn

import xyz.a00000.connectionserviceclient.raw.ConnectionServiceClient
import xyz.a00000.connectionserviceclient.raw.OnDataReadyRead
import xyz.a00000.connectionserviceclient.raw.OnConnectionException

class Connection(private val mConnectionClient: ConnectionServiceClient?, onDataReadyRead: OnDataReadyRead?, onConnectionException: OnConnectionException?) {

    init {
        mConnectionClient?.init(onDataReadyRead, onConnectionException)
    }

    fun sendData(data: ByteArray) {
        mConnectionClient?.write(data)
    }

    fun closeConnection() {
        mConnectionClient?.close()
    }

}