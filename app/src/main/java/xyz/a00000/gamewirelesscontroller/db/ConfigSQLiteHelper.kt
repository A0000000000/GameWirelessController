package xyz.a00000.gamewirelesscontroller.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import xyz.a00000.gamewirelesscontroller.activity.ConfigActivity

class ConfigSQLiteHelper(context: Context, version: Int): SQLiteOpenHelper(context, "config.db", null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.run {
            execSQL("create table t_config(id integer primary key autoincrement, config_key varchar(255), config_value varchar(255))")
            execSQL("insert into t_config(config_key, config_value) values (?, ?)", arrayOf(ConfigActivity.CONFIG_DEBUG, "0"))
            execSQL("insert into t_config(config_key, config_value) values (?, ?)", arrayOf(ConfigActivity.CONFIG_VOL, "1"))
            execSQL("insert into t_config(config_key, config_value) values (?, ?)", arrayOf(ConfigActivity.CONFIG_LEVEL, "0"))
            execSQL("insert into t_config(config_key, config_value) values (?, ?)", arrayOf(ConfigActivity.CONFIG_LITE_JOYSTICK, "0"))
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun putConfig(key: String, value: String) {
        val currentValue = getConfig(key)
        if (currentValue == null) {
            writableDatabase.execSQL("insert into t_config(config_key, config_value) values (?, ?)", arrayOf(key, value))
        } else {
            writableDatabase.execSQL("update t_config set config_value = ? where config_key = ?", arrayOf(value, key))
        }
    }

    fun getConfig(key: String): String? {
        val result = readableDatabase.rawQuery("select id, config_key, config_value from t_config where config_key = ?", arrayOf(key))
        var ans: String? = null
        if (result.moveToFirst()) {
            do {
                val keyIndex = result.getColumnIndex("config_key")
                val configKey = result.getString(keyIndex)
                val valueIndex = result.getColumnIndex("config_value")
                val configValue = result.getString(valueIndex)
                if (key == configKey && configValue != null && configValue.isNotEmpty()) {
                    ans = configValue
                    break
                }
            } while (result.moveToNext())
        }
        result.close()
        return ans
    }

}