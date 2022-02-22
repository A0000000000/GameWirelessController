package xyz.a00000.gamewirelesscontroller

import android.app.Activity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import xyz.a00000.bluetoothconnectlib.BluetoothConnection
import xyz.a00000.bluetoothconnectlib.BluetoothController
import xyz.a00000.bluetoothconnectlib.ConnectionFactory
import xyz.a00000.gamewirelesscontroller.bean.TransferObject
import xyz.a00000.gamewirelesscontroller.view.CrossKeyView
import xyz.a00000.gamewirelesscontroller.view.RockerView
import xyz.a00000.gamewirelesscontroller.view.SimpleButton
import xyz.a00000.gamewirelesscontroller.view.TriggerButton
import java.nio.charset.Charset
import kotlin.math.min


class ControllerActivity : Activity() {

    var mTvLog: TextView? = null
    var mTvTips: TextView? = null
    var mLeftButtonPanel: LinearLayout? = null
    var mRightButtonPanel: LinearLayout? = null
    var mLeftPanel: RelativeLayout? = null
    var mRightPanel: RelativeLayout? = null
    var mCenterPanel: RelativeLayout? = null

    val mBluetoothController: BluetoothController = BluetoothController.getInstance()
    var mTargetDevice: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        // TODO: 之后换没过时的接口
        window.decorView.systemUiVisibility =
            SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_FULLSCREEN

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
        mTvTips?.text = String.format("%s - %s", mTvTips?.text, mTargetDevice)
        initConnectionService()
    }

    private fun logKey(type: KeyType, action: Int, data: Map<String, Any>) {
        val log = "KeyType: ${type.name}, Action: $action, Data: $data"
        mTvLog?.text = log
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
        mLeftButton?.mCallback = object: SimpleButton.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.LEFT_BUTTON, action, HashMap())
            }

        }
        mRightButton?.mCallback = object: SimpleButton.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.RIGHT_BUTTON, action, HashMap())
            }

        }
        mLeftTrigger?.mCallback = object: TriggerButton.Callback {

            override fun press(trigger: Int, action: Int) {
                val data = HashMap<String, Any>()
                data["trigger"] = trigger
                inputComposer(KeyType.LEFT_TRIGGER, action, data)
            }

        }
        mRightTrigger?.mCallback = object: TriggerButton.Callback {

            override fun press(trigger: Int, action: Int) {
                val data = HashMap<String, Any>()
                data["trigger"] = trigger
                inputComposer(KeyType.RIGHT_TRIGGER, action, data)
            }

        }
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
        mLeftRockerView?.mCallback = object: RockerView.Callback {

            override fun click(action: Int) {
                val data = HashMap<String, Any>()
                data["type"] = "click"
                inputComposer(KeyType.LEFT_ROCKER, action, data)
            }

            override fun move(x: Int, y: Int, action: Int) {
                val data = HashMap<String, Any>()
                data["type"] = "move"
                data["x"] = x
                data["y"] = y
                inputComposer(KeyType.LEFT_ROCKER, action, data)
            }

        }
        mTopCrossKey?.mCallback = object: CrossKeyView.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.CROSS_TOP, action, HashMap())
            }

        }
        mLeftCrossKey?.mCallback = object: CrossKeyView.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.CROSS_LEFT, action, HashMap())
            }

        }
        mBottomCrossKey?.mCallback = object: CrossKeyView.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.CROSS_BOTTOM, action, HashMap())
            }

        }
        mRightCrossKey?.mCallback = object: CrossKeyView.Callback {

            override fun press(action: Int) {
                inputComposer(KeyType.CROSS_RIGHT, action, HashMap())
            }

        }
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
        mRightRockerView?.mCallback = object: RockerView.Callback {

            override fun click(action: Int) {
                val data = HashMap<String, Any>()
                data["type"] = "click"
                inputComposer(KeyType.RIGHT_ROCKER, action, data)
            }

            override fun move(x: Int, y: Int, action: Int) {
                val data = HashMap<String, Any>()
                data["type"] = "move"
                data["x"] = x
                data["y"] = y
                inputComposer(KeyType.RIGHT_ROCKER, action, data)
            }

        }
        mX?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.X, action, HashMap())
            }
        }
        mY?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.Y, action, HashMap())
            }
        }
        mA?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.A, action, HashMap())
            }
        }
        mB?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.B, action, HashMap())
            }
        }
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
        mMain?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.MAIN, action, HashMap())
            }
        }
        mFunction?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.FUNCTION, action, HashMap())
            }
        }
        mMenu?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.MENU, action, HashMap())
            }
        }
        mView?.mCallback = object: SimpleButton.Callback {
            override fun press(action: Int) {
                inputComposer(KeyType.VIEW, action, HashMap())
            }
        }
    }

    var mFirstVolumeDown = true

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            inputComposer(if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) KeyType.LEFT_BUTTON else KeyType.RIGHT_BUTTON, if (mFirstVolumeDown) 0 else 2, HashMap())
            if (mFirstVolumeDown) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    mLeftButton?.swapColor()
                } else {
                    mRightButton?.swapColor()
                }
            }
            mFirstVolumeDown = false
            return true;
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            inputComposer(if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) KeyType.LEFT_BUTTON else KeyType.RIGHT_BUTTON, 1, HashMap())
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mLeftButton?.swapColor()
            } else {
                mRightButton?.swapColor()
            }
            mFirstVolumeDown = true
            return true;
        }
        return super.onKeyUp(keyCode, event)
    }

    enum class KeyType {
        LEFT_BUTTON,
        RIGHT_BUTTON,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
        LEFT_ROCKER,
        RIGHT_ROCKER,
        CROSS_TOP,
        CROSS_LEFT,
        CROSS_RIGHT,
        CROSS_BOTTOM,
        X,
        Y,
        A,
        B,
        VIEW,
        MENU,
        MAIN,
        FUNCTION
    }

    private fun inputComposer(type: KeyType, action: Int, data: Map<String, Any>) {
        logKey(type, action, data)
        if (action == MotionEvent.ACTION_DOWN) {
            vibrate()
        }
        val composerData = HashMap<String, Any>()
        composerData["KeyType"] = type.name
        composerData["Action"] = when(action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> "UP"
            else -> "UNKNOWN"
        }
        composerData["data"] = data
        sendEventData(composerData)
    }

    private fun vibrate() {
        val vibrator = getSystemService(Vibrator::class.java)
        vibrator?.let {
            if (it.hasVibrator()) {
                it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    private fun sendEventData(data: Map<String, Any>) {
        try {
            mConnection?.sendData(
                TransferObject(data, 0, "Input Event").toJson()
                    .toByteArray(Charset.forName("UTF-8"))
            )
        } catch (e: Exception) {
            Toast.makeText(this, "事件发送失败: 原因: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        val UUID = "6ef82393-6cab-4749-b0b5-df0109fb7dec"
    }

    var mConnection: BluetoothConnection? = null

    fun initConnectionService() {
        try {
            mConnection = ConnectionFactory()
                .setBluetoothController(mBluetoothController)
                .setTargetDeviceName(mTargetDevice)
                .setUuid(UUID)
                .build()
            mConnection?.setOnReceive {
                val rev = String(it, Charset.forName("UTF-8"))
                if ("SUCCESS" != rev) {
                    runOnUiThread {
                        Toast.makeText(this, "接收到来自PC端意料之外的数据, rev = $rev", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            mConnection?.setOnDisconnect {
                runOnUiThread {
                    Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            mConnection?.openConnect()
        } catch (e: Exception) {
            Toast.makeText(this, "连接失败, 原因: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        try {
            mConnection?.close()
        } catch (ignore: Exception) { }
        super.onDestroy()
    }

}