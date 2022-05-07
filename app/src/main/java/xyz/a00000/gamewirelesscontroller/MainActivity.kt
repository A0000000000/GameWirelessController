package xyz.a00000.gamewirelesscontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import xyz.a00000.gamewirelesscontroller.activity.ConfigActivity
import xyz.a00000.gamewirelesscontroller.activity.JoystickActivity
import xyz.a00000.gamewirelesscontroller.activity.LiteJoystickActivity
import xyz.a00000.gamewirelesscontroller.db.ConfigSQLiteHelper
import xyz.a00000.gamewirelesscontroller.service.ConnectionService
import java.util.stream.Collectors

class MainActivity: AppCompatActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null

    private var mDevices: List<String>? = null

    private var mTvTitle: TextView? = null
    private var mLvDevices: ListView? = null
    private var mBtnController: Button? = null
    private var mTvFlush: TextView? = null

    private var mTargetDevice: String? = null

    private var mConnectionService: ConnectionService? = null

    private val mConfigSQLiteHelper = ConfigSQLiteHelper(this, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBluetoothAdapter = getSystemService(BluetoothManager::class.java).adapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 0)
            } else {
                mBluetoothAdapter?.run {
                    mDevices = bondedDevices.stream().map(BluetoothDevice::getName).collect(Collectors.toList())
                }
            }
        }
        mBluetoothAdapter?.run {
            if (!isEnabled) {
                if (!enable()) {
                    Toast.makeText(this@MainActivity, "请先打开蓝牙!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        bindService(Intent(this, ConnectionService::class.java), object: ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                mConnectionService = (p1 as ConnectionService.LocalBinder).getService()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }
        }, Context.BIND_AUTO_CREATE)
        mTvTitle = findViewById(R.id.tv_title)
        mTvFlush = findViewById(R.id.tv_flush)
        mLvDevices = findViewById(R.id.lv_devices)
        mBtnController = findViewById(R.id.btn_controller)
        mTvFlush?.setOnClickListener {
            mBluetoothAdapter?.run {
                mDevices = bondedDevices.stream().map(BluetoothDevice::getName).collect(Collectors.toList())
            }
            initListViewData()
        }
        initListViewData()
        mBtnController?.setOnClickListener {
            if (mConnectionService == null) {
                Toast.makeText(this@MainActivity, "服务未启动, 请稍后重试.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mConnectionService?.mTargetDeviceName = mTargetDevice
            if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_LITE_JOYSTICK) != "1") {
                startActivity(Intent(this@MainActivity, JoystickActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, LiteJoystickActivity::class.java))
            }
        }
        mTvTitle?.setOnLongClickListener {
            startActivity(Intent(this@MainActivity, ConfigActivity::class.java))
            return@setOnLongClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        if (mConfigSQLiteHelper.getConfig(ConfigActivity.CONFIG_DEBUG) == "1") {
            mBtnController?.isEnabled = true
            mBtnController?.text = "=>"
        } else {
            mBtnController?.isEnabled = false
            mBtnController?.text = "=>"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "缺少必要权限!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                mBluetoothAdapter?.run {
                    mDevices = bondedDevices.stream().map(BluetoothDevice::getName).collect(Collectors.toList())
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initListViewData() {
        mDevices?.let {
            val data = ArrayList(it)
            mLvDevices?.adapter =
                ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, data)
            mLvDevices?.setOnItemClickListener { _, view, _, _ ->
                if (view is MaterialTextView) {
                    mTargetDevice = view.text.toString()
                    mBtnController?.isEnabled = true
                    mBtnController?.text = String.format("%s =>", mTargetDevice)
                }
                if (view is TextView) {
                    mTargetDevice = view.text.toString()
                    mBtnController?.isEnabled = true
                    mBtnController?.text = String.format("%s =>", mTargetDevice)
                }
            }
        }
    }
}

