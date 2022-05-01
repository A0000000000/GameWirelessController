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
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import xyz.a00000.joystickcustomview.view.*
import kotlin.math.min


class JoystickActivity : AppCompatActivity() {

    private var mTvLog: TextView? = null
    private var mTvTips: TextView? = null
    private var mLeftButtonPanel: LinearLayout? = null
    private var mRightButtonPanel: LinearLayout? = null
    private var mLeftPanel: RelativeLayout? = null
    private var mRightPanel: RelativeLayout? = null
    private var mCenterPanel: RelativeLayout? = null
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
        mLeftButtonPanel = findViewById(R.id.left_button_panel)
        mRightButtonPanel = findViewById(R.id.right_button_panel)
        mLeftPanel = findViewById(R.id.left_panel)
        mRightPanel = findViewById(R.id.right_panel)
        mCenterPanel = findViewById(R.id.center_panel)

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
            mTvTips?.text = String.format("%s - %s", TIPS, mConnectionService?.mTargetDeviceName)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        initCustomizationView()
        super.onWindowFocusChanged(hasFocus)
    }


    private fun initCustomizationView() {
        initTriggerAndButton()
        initLeftPanel()
        initRightPanel()
        initCenterButton()
    }

    private var mLeftButton: SimpleButton? = null
    private var mRightButton: SimpleButton? = null
    private var mLeftTrigger: TriggerButton? = null
    private var mRightTrigger: TriggerButton? = null

    private fun initTriggerAndButton() {
        mLeftButton = findViewById(R.id.sb_lb)
        mRightButton = findViewById(R.id.sb_rb)
        mLeftTrigger = findViewById(R.id.tb_lt)
        mRightTrigger = findViewById(R.id.tb_rt)
        mLeftButtonPanel?.let {
            val lbp = it.width
            mLeftButton?.mRadius = lbp / 10
            mLeftTrigger?.mLength = lbp / 10 * 8
            mLeftTrigger?.mHigh = lbp / 10 * 2
        }
        mRightButtonPanel?.let {
            val rbp = it.width
            mRightButton?.mRadius = rbp / 10
            mRightTrigger?.mLength = rbp / 10 * 8
            mRightTrigger?.mHigh = rbp / 10 * 2
        }

        mLeftButton?.mCallback = mCallback
        mRightButton?.mCallback = mCallback
        mLeftTrigger?.mCallback = mCallback
        mRightTrigger?.mCallback = mCallback
    }

    private var mLeftRockerView: RockerView? = null
    private var mTopCrossKey: CrossKeyView? = null
    private var mLeftCrossKey: CrossKeyView? = null
    private var mBottomCrossKey: CrossKeyView? = null
    private var mRightCrossKey: CrossKeyView? = null

    private fun initLeftPanel() {
        mLeftRockerView = findViewById(R.id.rv_left)
        mTopCrossKey = findViewById(R.id.ckv_t)
        mLeftCrossKey = findViewById(R.id.ckv_l)
        mBottomCrossKey = findViewById(R.id.ckv_b)
        mRightCrossKey = findViewById(R.id.ckv_r)
        mLeftPanel?.let {
            val width = it.width
            val height = it.height
            mLeftRockerView?.mRadius = min(height.toFloat() / 4, width.toFloat() / 2)
            val length = min(height / 6,width / 3)
            mTopCrossKey?.mLong = length
            mLeftCrossKey?.mLong = length
            mBottomCrossKey?.mLong = length
            mRightCrossKey?.mLong = length
            mTopCrossKey?.mWide = length
            mLeftCrossKey?.mWide = length
            mBottomCrossKey?.mWide = length
            mRightCrossKey?.mWide = length
        }

        mLeftRockerView?.mCallback = mCallback
        mTopCrossKey?.mCallback = mCallback
        mLeftCrossKey?.mCallback = mCallback
        mBottomCrossKey?.mCallback = mCallback
        mRightCrossKey?.mCallback = mCallback
    }

    private var mRightRockerView: RockerView? = null
    private var mX: SimpleButton? = null
    private var mY: SimpleButton? = null
    private var mA: SimpleButton? = null
    private var mB: SimpleButton? = null

    private fun initRightPanel() {
        mRightRockerView = findViewById(R.id.rv_right)
        mX = findViewById(R.id.sb_x)
        mY = findViewById(R.id.sb_y)
        mA = findViewById(R.id.sb_a)
        mB = findViewById(R.id.sb_b)
        mRightPanel?.let {
            val width = it.width
            val height = it.height
            mLeftRockerView?.mRadius = min(height.toFloat() / 4, width.toFloat() / 2)
            val length = min(height / 6,width / 3) / 2
            mX?.mRadius = length
            mY?.mRadius = length
            mA?.mRadius = length
            mB?.mRadius = length
        }

        mRightRockerView?.mCallback = mCallback
        mX?.mCallback = mCallback
        mY?.mCallback = mCallback
        mA?.mCallback = mCallback
        mB?.mCallback = mCallback
    }

    private var mMain: SimpleButton? = null
    private var mFunction: SimpleButton? = null
    private var mView: SimpleButton? = null
    private var mMenu: SimpleButton? = null

    private fun initCenterButton() {
        mMain = findViewById(R.id.sb_main)
        mFunction = findViewById(R.id.sb_function)
        mView = findViewById(R.id.sb_view)
        mMenu = findViewById(R.id.sb_menu)
        mCenterPanel?.let {
            val radius = min(it.width, it.height) / 5
            mMain?.mRadius = radius
            mFunction?.mRadius = radius
            mView?.mRadius = radius
            mMenu?.mRadius = radius
        }
        mMain?.mCallback = mCallback
        mFunction?.mCallback = mCallback
        mMenu?.mCallback = mCallback
        mView?.mCallback = mCallback
    }

    private var mFirstVolumeDown = true

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (ENABLE) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (mFirstVolumeDown) {
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                        mLeftButton?.swapColor()
                        mLeftButton?.mCallback?.event(GameEvent.createKeyEvent(Xbox360Type.Companion.KeyType.LEFT_BUTTON, 0))
                    } else {
                        mRightButton?.swapColor()
                        mLeftButton?.mCallback?.event(GameEvent.createKeyEvent(Xbox360Type.Companion.KeyType.RIGHT_BUTTON, 0))
                    }
                }
                mFirstVolumeDown = false
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (ENABLE) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    mLeftButton?.swapColor()
                    mLeftButton?.mCallback?.event(GameEvent.createKeyEvent(Xbox360Type.Companion.KeyType.LEFT_BUTTON, 1))
                } else {
                    mRightButton?.swapColor()
                    mLeftButton?.mCallback?.event(GameEvent.createKeyEvent(Xbox360Type.Companion.KeyType.RIGHT_BUTTON, 1))
                }
                mFirstVolumeDown = true
                return true
            }
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


    companion object {
        @JvmStatic
        val TIPS = "Game Wireless Controller"
        @JvmStatic
        val ENABLE = true
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