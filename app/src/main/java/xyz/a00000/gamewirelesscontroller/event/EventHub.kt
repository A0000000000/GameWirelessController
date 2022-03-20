package xyz.a00000.gamewirelesscontroller.event

import xyz.a00000.connectionserviceclient.ConnectionServiceClientDecorator
import xyz.a00000.connectionserviceclient.OnDataReadyReadDecorator
import xyz.a00000.connectionserviceclient.internal.OnReadException
import xyz.a00000.gamewirelesscontroller.bean.TransferObject
import xyz.a00000.joystickcustomview.bean.GameEvent
import java.io.IOException

class EventHub constructor(var client: ConnectionServiceClientDecorator<TransferObject>?,
                           var onEventReady: OnEventReady?,
                           var onDisconnected: OnDisconnected?,
                           onDataReadyReadDecorator: OnDataReadyReadDecorator<TransferObject?>?,
                           onReadException: OnReadException?): AutoCloseable {

    init {
        client?.init(onDataReadyReadDecorator, onReadException, {
            onDisconnected?.onDisconnected()
        })
    }

    var target: ConnectionTarget = ConnectionTarget(client)

    fun postGameEvent(ev: GameEvent) {
        try {
            val obj = TransferObject(ev, TransferObject.TYPE_GAME_EVENT, null)
            target.sendTransferObject(obj)
            onEventReady?.eventReady(ev)
        } catch (e: IOException) {
            onDisconnected?.onDisconnected()
        }
    }

    interface OnEventReady {
        fun eventReady(ev: GameEvent)
    }

    interface OnDisconnected {
        fun onDisconnected()
    }

    override fun close() {
        client?.close()
    }

}