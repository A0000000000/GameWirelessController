package xyz.a00000.gamewirelesscontroller.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import xyz.a00000.gamewirelesscontroller.R
import xyz.a00000.gamewirelesscontroller.bean.JoystickEvent
import xyz.a00000.gamewirelesscontroller.service.ConnectionService
import xyz.a00000.joystickcustomview.bean.GameEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.group.*
import xyz.a00000.joystickcustomview.view.*


class JoystickActivity : AppCompatActivity() {

    private var mTvLog: TextView? = null
    private var mTvTips: TextView? = null

    private var mSensorManager: SensorManager? = null
    private var mSensor:Sensor? = null
    private var mConnectionService: ConnectionService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)
        hideSystemBars()
        bindService(Intent(this, ConnectionService::class.java), object: ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                runOnUiThread {
                    Toast.makeText(this@JoystickActivity, "服务获取成功, 准备初始化.", Toast.LENGTH_SHORT).show()
                }
                mConnectionService = (p1 as ConnectionService.LocalBinder).getService()
                mConnectionService?.mOnConnectedSuccess = Runnable {
                    runOnUiThread {
                        Toast.makeText(this@JoystickActivity, "连接成功.", Toast.LENGTH_SHORT).show()
                    }
                }
                mConnectionService?.mOnConnectedFailed = Runnable {
                    runOnUiThread {
                        Toast.makeText(this@JoystickActivity, "连接失败, 请检查服务端是否启动.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                initEventSystem()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }
        }, Context.BIND_AUTO_CREATE)

        mTvLog = findViewById(R.id.tv_log)
        mTvTips = findViewById(R.id.tv_tips)
        mTvLog?.setOnClickListener {
            (it as TextView).text = ""
        }

        initCustomizationViewEvent()
        initSensorSystem()

    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        hideSystemBars()
        return super.onTouchEvent(event)
    }

    private val mCallback: Callback = object : Callback {
        override fun event(ev: GameEvent) {
            hideSystemBars()
            onGameEvent(ev)
        }
    }

    private fun initEventSystem() {
        Thread {
            mConnectionService?.setInitData(object: ConnectionService.OnJoystickEvent {
                override fun onJoystickEvent(ev: JoystickEvent?) {
                    runOnUiThread {
                        this@JoystickActivity.onJoystickEvent(ev)
                    }
                }
            })
        }.start()
        runOnUiThread {
            mTvTips?.text = String.format("Game Wireless Controller - %s", mConnectionService?.mTargetDeviceName)
        }
    }

    private var mSensorEventListener = object: SensorEventListener{
        override fun onSensorChanged(e: SensorEvent?) {
            if (e?.accuracy != 0) {
                if (e?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
                    // Todo: todo something
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private fun initSensorSystem() {
        mSensorManager = getSystemService(SensorManager::class.java)
        mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager?.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun initCustomizationViewEvent() {
        findViewById<LeftTopGroup>(R.id.ltg)?.setCallback(mCallback)
        findViewById<Rocker>(R.id.r_left_rocker)?.mCallback = mCallback
        findViewById<CrossKeyGroup>(R.id.ckg)?.setCallback(mCallback)
        findViewById<OperatorPanelGroup>(R.id.opg)?.setCallback(mCallback)
        findViewById<RightTopGroup>(R.id.rtg)?.setCallback(mCallback)
        findViewById<ABXYGroup>(R.id.abxy)?.setCallback(mCallback)
        findViewById<Rocker>(R.id.r_right_rocker)?.mCallback = mCallback
    }

    private var mFirstVolumeDown = true

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mFirstVolumeDown) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mCallback.event(xyz.a00000.joystickcustomview.bean.KeyEvent(0, Xbox360Type.Companion.KeyType.LEFT_BUTTON))
                mFirstVolumeDown = false
                return true
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mCallback.event(xyz.a00000.joystickcustomview.bean.KeyEvent(0, Xbox360Type.Companion.KeyType.RIGHT_BUTTON))
                mFirstVolumeDown = false
                return true
            }
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mCallback.event(xyz.a00000.joystickcustomview.bean.KeyEvent(1, Xbox360Type.Companion.KeyType.LEFT_BUTTON))
            mFirstVolumeDown = true
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mCallback.event(xyz.a00000.joystickcustomview.bean.KeyEvent(1, Xbox360Type.Companion.KeyType.RIGHT_BUTTON))
            mFirstVolumeDown = true
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun vibrate() {
        val vibrator = getSystemService(Vibrator::class.java)
        vibrator?.let {
            if (it.hasVibrator()) {
                it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    private fun onGameEvent(ev: GameEvent) {
        mConnectionService?.sendGameEvent(ev)
        if (ev.eventType != Xbox360Type.AXIS && ev.eventType != Xbox360Type.TRIGGER) {
             vibrate()
        }
    }

    private fun onJoystickEvent(ev: JoystickEvent?) {
        mTvLog?.text = ev?.toString()
    }

    override fun onPause() {
        mSensorManager?.unregisterListener(mSensorEventListener)
        super.onPause()
    }

    override fun onDestroy() {
        mConnectionService?.closeConnection()
        super.onDestroy()
    }

}