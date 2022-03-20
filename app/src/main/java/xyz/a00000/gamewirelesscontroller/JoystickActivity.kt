package xyz.a00000.gamewirelesscontroller

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import xyz.a00000.connectionserviceclient.ConnectionServiceClientDecorator
import xyz.a00000.connectionserviceclient.internal.ConnectionServiceController
import xyz.a00000.connectionserviceclient.internal.ConnectionServiceFactory
import xyz.a00000.gamewirelesscontroller.bean.JoystickEvent
import xyz.a00000.gamewirelesscontroller.bean.TransferObject
import xyz.a00000.gamewirelesscontroller.event.EventHub
import xyz.a00000.joystickcustomview.bean.GameEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.view.*
import kotlin.math.min


class JoystickActivity : AppCompatActivity() {

    var mTvLog: TextView? = null
    var mTvTips: TextView? = null
    var mLeftButtonPanel: LinearLayout? = null
    var mRightButtonPanel: LinearLayout? = null
    var mLeftPanel: RelativeLayout? = null
    var mRightPanel: RelativeLayout? = null
    var mCenterPanel: RelativeLayout? = null
    var mTargetDevice: String? = null
    var mSensorManager: SensorManager? = null
    var mSensor:Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }

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
        mTargetDevice = intent.getStringExtra("targetDevice")
        mTvTips?.text = String.format("%s - %s", TIPS, mTargetDevice)

        initEventSystem()
        initSensorSystem()

    }

    var mConnectionController = ConnectionServiceController.getInstance()
    var mConnectionFactory: ConnectionServiceFactory? = null
    var mConnectionServiceClient: ConnectionServiceClientDecorator<TransferObject>? = null
    var mEventHub: EventHub? = null
    var mCallback: Callback? = object : Callback {
        override fun event(ev: GameEvent) {
            mEventHub?.postGameEvent(ev)
        }
    }

    private fun initEventSystem() {
        Thread {
            try {
                mConnectionFactory = mConnectionController.createServiceFactory(mTargetDevice)
                mConnectionServiceClient = ConnectionServiceClientDecorator(
                    Settings.Global.DEVICE_NAME,
                    mConnectionFactory?.getConnectionServiceClient(UUID)
                )
                mEventHub = EventHub(mConnectionServiceClient, object : EventHub.OnEventReady {
                    override fun eventReady(ev: GameEvent) {
                        runOnUiThread {
                            onGameEvent(ev)
                        }
                    }
                }, object : EventHub.OnDisconnected {
                    override fun onDisconnected() {
                        finish()
                    }
                }, {
                    it?.let { to ->
                        if (to.type == TransferObject.TYPE_JOYSTICK_EVENT) {
                            runOnUiThread {
                                onJoystickEvent(to.joystickEvent)
                            }
                        }
                    }
                }, {

                })
                runOnUiThread {
                    Toast.makeText(
                        this@JoystickActivity,
                        "初始化完成!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@JoystickActivity,
                        "连接失败, 请检查服务端是否已经启动.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        }.start()
    }

    var mSensorEventListener = object: SensorEventListener{
        override fun onSensorChanged(e: SensorEvent?) {
            if (e?.accuracy != 0) {
                if (e?.sensor?.type == Sensor.TYPE_GYROSCOPE) {

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

    var mLeftButton: SimpleButton? = null
    var mRightButton: SimpleButton? = null
    var mLeftTrigger: TriggerButton? = null
    var mRightTrigger: TriggerButton? = null

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

    var mLeftRockerView: RockerView? = null
    var mTopCrossKey: CrossKeyView? = null
    var mLeftCrossKey: CrossKeyView? = null
    var mBottomCrossKey: CrossKeyView? = null
    var mRightCrossKey: CrossKeyView? = null

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

    var mRightRockerView: RockerView? = null
    var mX: SimpleButton? = null
    var mY: SimpleButton? = null
    var mA: SimpleButton? = null
    var mB: SimpleButton? = null

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

    var mMain: SimpleButton? = null
    var mFunction: SimpleButton? = null
    var mView: SimpleButton? = null
    var mMenu: SimpleButton? = null

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

    var mFirstVolumeDown = true

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
        val UUID = "6ef82393-6cab-4749-b0b5-df0109fb7dec"
        @JvmStatic
        val TAG = "ControllerActivity"
        @JvmStatic
        val TIPS = "Game Wireless Controller"
        @JvmStatic
        val ENABLE = true
    }

    private fun onGameEvent(ev: GameEvent) {
//        mTvLog?.text = JsonUtils.toJson(ev)
    }

    private fun onJoystickEvent(ev: JoystickEvent?) {
        mTvLog?.text = ev?.toString()
    }


    override fun onPause() {
        mSensorManager?.unregisterListener(mSensorEventListener)
        super.onPause()
    }

    override fun onDestroy() {
        mEventHub?.close()
        super.onDestroy()
    }

}