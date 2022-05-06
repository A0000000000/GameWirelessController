package xyz.a00000.gamewirelesscontroller.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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
import xyz.a00000.gamewirelesscontroller.activity.listen.KeyEventListener
import xyz.a00000.gamewirelesscontroller.activity.listen.LevelListener
import xyz.a00000.gamewirelesscontroller.bean.JoystickEvent
import xyz.a00000.gamewirelesscontroller.db.ConfigSQLiteHelper
import xyz.a00000.gamewirelesscontroller.service.ConnectionService
import xyz.a00000.joystickcustomview.bean.GameEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.group.*
import xyz.a00000.joystickcustomview.view.*


class JoystickActivity : AppCompatActivity() {

    private val mConfigSQLiteHelper = ConfigSQLiteHelper(this, 1)

    private var mTvLog: TextView? = null
    private var mTvTips: TextView? = null

    private var mSensorManager: SensorManager? = null
    private var mConnectionService: ConnectionService? = null

    private var mKeyEventListener: KeyEventListener? = null
    private var mLevelListener: LevelListener? = null

    private val mCallback: Callback = object : Callback {
        override fun event(ev: GameEvent) {
            hideSystemBars()
            onGameEvent(ev)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        hideSystemBars()
        initListener()
        initConnectionService()
        initCustomizationViewEvent()

    }

    override fun onResume() {
        super.onResume()
        mLevelListener?.initSensorSystem()
    }

    override fun onPause() {
        super.onPause()
        mLevelListener?.disposeSensorSystem()
    }

    override fun onDestroy() {
        mConnectionService?.closeConnection()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        hideSystemBars()
        return super.onTouchEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((mKeyEventListener?.onKeyDown(keyCode, event) == true)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if ((mKeyEventListener?.onKeyUp(keyCode, event) == true)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    private fun initConnectionService() {
        if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_DEBUG) == "1") {
            return
        }
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

    private fun initCustomizationViewEvent() {
        mTvLog = findViewById(R.id.tv_log)
        mTvTips = findViewById(R.id.tv_tips)
        mTvLog?.setOnClickListener {
            (it as TextView).text = ""
        }
        findViewById<LeftTopGroup>(R.id.ltg)?.setCallback(mCallback)
        findViewById<Rocker>(R.id.r_left_rocker)?.mCallback = mCallback
        findViewById<CrossKeyGroup>(R.id.ckg)?.setCallback(mCallback)
        findViewById<OperatorPanelGroup>(R.id.opg)?.setCallback(mCallback)
        findViewById<RightTopGroup>(R.id.rtg)?.setCallback(mCallback)
        findViewById<ABXYGroup>(R.id.abxy)?.setCallback(mCallback)
        findViewById<Rocker>(R.id.r_right_rocker)?.mCallback = mCallback
    }

    private fun initListener() {
        if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_VOL) == "1") {
            mKeyEventListener = KeyEventListener(mCallback)
        }
        if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_LEVEL) == "1") {
            mSensorManager = getSystemService(SensorManager::class.java)
            mLevelListener = LevelListener(mCallback, mSensorManager)
        }
    }

    private fun vibrate(milliseconds: Long) {
        val vibrator = getSystemService(Vibrator::class.java)
        vibrator?.let {
            if (it.hasVibrator()) {
                it.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    private fun onGameEvent(ev: GameEvent) {
        mConnectionService?.sendGameEvent(ev)
        if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_DEBUG) == "1") {
            mTvLog?.text = ev.toString()
        }
        if (ev.eventType != Xbox360Type.AXIS && ev.eventType != Xbox360Type.TRIGGER) {
             vibrate(20)
        }
    }

    private fun onJoystickEvent(ev: JoystickEvent?) {
        mTvLog?.text = ev?.toString()
    }

}