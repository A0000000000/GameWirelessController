package xyz.a00000.gamewirelesscontroller.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import xyz.a00000.connectionserviceclient.ConnectionServiceController
import xyz.a00000.connectionserviceclient.raw.ConnectionServiceFactory
import xyz.a00000.connectionserviceclient.raw.OnDataReadyRead
import xyz.a00000.connectionserviceclient.raw.OnConnectionException
import xyz.a00000.connectionserviceclient.utils.JsonUtils
import xyz.a00000.gamewirelesscontroller.bean.JoystickEvent
import xyz.a00000.gamewirelesscontroller.config.Config
import xyz.a00000.gamewirelesscontroller.conn.Connection
import xyz.a00000.joystickcustomview.bean.GameEvent
import java.lang.Exception
import java.nio.charset.StandardCharsets

class ConnectionService : Service() {

    var mTargetDeviceName: String? = null
        set(value) {
            field = value
            mConnectionServiceFactory = mConnectionServiceController.createServiceFactory(value)
        }
    private var mConnectionServiceFactory: ConnectionServiceFactory? = null
    private val mConnectionServiceController = ConnectionServiceController.getInstance()
    private val mBinder = LocalBinder()
    private var onDataReadyRead: OnDataReadyRead? = null
    private var onConnectionException: OnConnectionException? = null
    private var mCurrentConnection: Connection? = null
    var mOnConnectedSuccess: Runnable? = null
    var mOnConnectedFailed: Runnable? = null



    fun sendGameEvent(ev: GameEvent) {
        val json = JsonUtils.toJson(ev)
        json?.run {
            mCurrentConnection?.sendData(toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun setInitData(onJoystickEvent: OnJoystickEvent) {
        setInitDataInner { data ->
            data?.let {
                val json = String(data, StandardCharsets.UTF_8)
                val ev = JsonUtils.fromJson(json, JoystickEvent::class.java)
                ev?.let {
                    onJoystickEvent.onJoystickEvent(ev)
                }
            }
        }
    }

    fun closeConnection() {
        mCurrentConnection?.closeConnection()
        mCurrentConnection = null
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        mCurrentConnection?.closeConnection()
        super.onDestroy()
    }

    private fun setInitDataInner(onDataReadyRead: OnDataReadyRead) {
        this.onDataReadyRead = onDataReadyRead
        this.onConnectionException =
            OnConnectionException {
                mOnConnectedFailed?.run()
                mCurrentConnection?.closeConnection()
            }
        mCurrentConnection = getConnection()
    }

    private fun getConnection(): Connection? {
        try {
            val connectionServiceClient = mConnectionServiceFactory?.getConnectionServiceClient(Config.UUID)
            val ans = Connection(connectionServiceClient, onDataReadyRead, onConnectionException)
            mOnConnectedSuccess?.run()
            return ans
        } catch (e: Exception) {
            mOnConnectedFailed?.run()
        }
        return null
    }


    inner class LocalBinder: Binder() {
        fun getService(): ConnectionService {
            return this@ConnectionService
        }
    }

    interface OnJoystickEvent {
        fun onJoystickEvent(ev: JoystickEvent?)
    }

}