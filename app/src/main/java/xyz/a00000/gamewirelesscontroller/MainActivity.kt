package xyz.a00000.gamewirelesscontroller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import xyz.a00000.bluetoothconnectlib.BluetoothController

class MainActivity : AppCompatActivity() {

    private val mBluetoothController: BluetoothController = BluetoothController.getInstance()

    var mDevices: Set<String>? = null

    var mTvTitle: TextView? = null
    var mLvDevices: ListView? = null
    var mBtnController: Button? = null
    var mTvFlush: TextView? = null

    var mTargetDevice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 0);
            }
        }
        if (!mBluetoothController.isSupportBluetooth) {
            if (!mBluetoothController.enable()) {
                Toast.makeText(this, "请先打开蓝牙!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        mDevices = mBluetoothController.pairedDevicesName
        mTvTitle = findViewById(R.id.tv_title)
        mTvFlush = findViewById(R.id.tv_flush)
        mLvDevices = findViewById(R.id.lv_devices)
        mBtnController = findViewById(R.id.btn_controller)
        mTvFlush?.setOnClickListener {
            mDevices = mBluetoothController.pairedDevicesName
            initListViewData()
        }
        initListViewData()
        mBtnController?.setOnClickListener {
            val controllerIntent = Intent(this@MainActivity, ControllerActivity::class.java)
            controllerIntent.putExtra("targetDevice", mTargetDevice)
            startActivity(controllerIntent)
        }
        mBtnController?.isEnabled = false
        mBtnController?.text = "=>"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "缺少必要权限!", Toast.LENGTH_SHORT).show()
                finish()
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

