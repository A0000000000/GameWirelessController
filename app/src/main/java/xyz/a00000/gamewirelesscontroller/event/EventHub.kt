package xyz.a00000.gamewirelesscontroller.event

import android.provider.Settings
import xyz.a00000.connectionserviceclient.ConnectionServiceClientDecorator
import xyz.a00000.connectionserviceclient.internal.ConnectionServiceController
import xyz.a00000.connectionserviceclient.internal.ConnectionServiceFactory
import xyz.a00000.connectionserviceclient.internal.OnReadException
import xyz.a00000.gamewirelesscontroller.bean.JoystickEvent
import xyz.a00000.gamewirelesscontroller.bean.TransferObject
import xyz.a00000.gamewirelesscontroller.config.Config
import xyz.a00000.joystickcustomview.bean.GameEvent
import java.io.IOException

class EventHub constructor(var onDisconnected: OnDisconnected?,
                           onJoystickEvent: OnJoystickEvent,
                           onReadException: OnReadException?): AutoCloseable {


    var mConnectionController = ConnectionServiceController.getInstance()
    var mConnectionFactory: ConnectionServiceFactory? = null
    var mConnectionServiceClient: ConnectionServiceClientDecorator<TransferObject>? = null

    init {
        mConnectionFactory = mConnectionController.createServiceFactory(Config.DEVICE_NAME)
        mConnectionServiceClient = ConnectionServiceClientDecorator(
            Settings.Global.DEVICE_NAME,
            mConnectionFactory?.getConnectionServiceClient(Config.UUID)
        )

        mConnectionServiceClient?.init(TransferObject::class.java, { it ->
            if (it.type == TransferObject.TYPE_JOYSTICK_EVENT) {
                onJoystickEvent.onJoystickEvent(it.joystickEvent)
            }
        }, onReadException)
    }

    var target: ConnectionTarget = ConnectionTarget(mConnectionServiceClient)

    fun postGameEvent(ev: GameEvent) {
        try {
            val obj = TransferObject(ev, TransferObject.TYPE_GAME_EVENT, null)
            target.sendTransferObject(obj)
        } catch (e: IOException) {
            onDisconnected?.onDisconnected()
        }
    }

    interface OnDisconnected {
        fun onDisconnected()
    }

    interface OnJoystickEvent {
        fun onJoystickEvent(je: JoystickEvent?)
    }

    override fun close() {
        mConnectionServiceClient?.close()
    }

}