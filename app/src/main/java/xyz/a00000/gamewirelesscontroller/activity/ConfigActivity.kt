package xyz.a00000.gamewirelesscontroller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import xyz.a00000.gamewirelesscontroller.R
import xyz.a00000.gamewirelesscontroller.db.ConfigSQLiteHelper

class ConfigActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        val CONFIG_DEBUG = "debug"
        @JvmStatic
        val CONFIG_VOL = "vol"
        @JvmStatic
        val CONFIG_LEVEL = "level"
        @JvmStatic
        val CONFIG_LITE_JOYSTICK = "lite_joystick"
    }

    private val mConfigSQLiteHelper = ConfigSQLiteHelper(this, 1)

    private var mConfigTitle: TextView? = null
    private var mDebugConfig: CheckBox? = null
    private var mVolConfig: CheckBox? = null
    private var mLevelConfig: CheckBox? = null
    private var mLiteJoystickConfig: CheckBox? = null

    private var mDebugValue = false
    private var mVolValue = true
    private var mLevelValue = false
    private var mLiteValue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        mConfigTitle = findViewById(R.id.tv_config_title)
        mDebugConfig = findViewById(R.id.cb_debug_config)
        mVolConfig = findViewById(R.id.cb_vol_config)
        mLevelConfig = findViewById(R.id.cb_level_config)
        mLiteJoystickConfig = findViewById(R.id.cb_lite_joystick_config)

        restoreConfig()
        showConfig()

        if (mLiteValue) {
            mVolConfig?.isEnabled = false
            mLevelConfig?.isEnabled = false
        }

        mDebugConfig?.setOnCheckedChangeListener { _, b ->
            mDebugValue = b
            saveConfig()
        }

        mVolConfig?.setOnCheckedChangeListener { _, b ->
            mVolValue = b
            saveConfig()
        }

        mLevelConfig?.setOnCheckedChangeListener { _, b ->
            mLevelValue = b
            saveConfig()
        }

        mLiteJoystickConfig?.setOnCheckedChangeListener { _, b ->
            mLiteValue = b
            if (b) {
                mVolValue = b
                mLevelValue = b
                mVolConfig?.isEnabled = false
                mLevelConfig?.isEnabled = false
            } else {
                mVolConfig?.isEnabled = true
                mLevelConfig?.isEnabled = true
            }
            showConfig()
            saveConfig()
        }

    }

    private fun showConfig() {
        mDebugConfig?.isChecked = mDebugValue
        mVolConfig?.isChecked = mVolValue
        mLevelConfig?.isChecked = mLevelValue
        mLiteJoystickConfig?.isChecked = mLiteValue
    }

    private fun saveConfig() {
        mConfigSQLiteHelper.run {
            putConfig(CONFIG_DEBUG, change(mDebugValue))
            putConfig(CONFIG_VOL, change(mVolValue))
            putConfig(CONFIG_LEVEL, change(mLevelValue))
            putConfig(CONFIG_LITE_JOYSTICK, change(mLiteValue))
        }
    }
    private fun restoreConfig() {
        mConfigSQLiteHelper.run {
            mDebugValue = change(getConfig(CONFIG_DEBUG))
            mVolValue = change(getConfig(CONFIG_VOL))
            mLevelValue = change(getConfig(CONFIG_LEVEL))
            mLiteValue = change(getConfig(CONFIG_LITE_JOYSTICK))
        }
    }

    private fun change(value: Boolean): String {
        return if (value) "1" else "0"
    }

    private fun change(value: String?): Boolean {
        return value == "1"
    }

}