package xyz.a00000.gamewirelesscontroller.event

import xyz.a00000.connectionserviceclient.ConnectionServiceClientDecorator
import xyz.a00000.gamewirelesscontroller.bean.TransferObject

class ConnectionTarget constructor(client: ConnectionServiceClientDecorator<TransferObject>?) {

    private var target: ConnectionServiceClientDecorator<TransferObject>? = client

    fun sendTransferObject(obj: TransferObject) {
        target?.write(obj)
    }

}