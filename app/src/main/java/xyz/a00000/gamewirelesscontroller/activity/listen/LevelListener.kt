package xyz.a00000.gamewirelesscontroller.activity.listen

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import xyz.a00000.joystickcustomview.bean.RockerEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.view.Callback

class LevelListener(private val mCallback: Callback, private val mSensorManager: SensorManager?) {

    private var mSensorEventListener = object: SensorEventListener {

        private var mAccelerometerReading = floatArrayOf(0f, 0f, 0f)
        private var mMagnetometerReading = floatArrayOf(0f, 0f, 0f)
        private var mRotationMatrix = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        private var mOrientationAngles = floatArrayOf(0f, 0f, 0f)

        override fun onSensorChanged(e: SensorEvent?) {
            e?.run {
                sensor?.run {
                    when (type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            System.arraycopy(values, 0, mAccelerometerReading, 0, mAccelerometerReading.size)
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            System.arraycopy(values, 0, mMagnetometerReading, 0, mMagnetometerReading.size)
                        }
                        else -> {

                        }
                    }
                    SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading)
                    SensorManager.getOrientation(mRotationMatrix, mOrientationAngles)
                    onAngleChange(-mOrientationAngles[1], -mOrientationAngles[2])
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }

    private var mFlag = true

    private fun onAngleChange(leftToRight: Float, topToBottom: Float) {
        var x = toPercentage(fixRange(leftToRight))
        var y = toPercentage(fixRange(topToBottom))
        if (x < 10 && x > -10) {
            x = 0
        }
        if (y < 10 && y > -10) {
            y = 0
        }
        x *= 2
        y *= 2
        if (x > 100) {
            x = 100
        }
        if (x < -100) {
            x = -100
        }
        if (y > 100) {
            y = 100
        }
        if (y < -100) {
            y = -100
        }
        if (x != 0 || y != 0) {
            mCallback.event(RockerEvent(x, y, Xbox360Type.Companion.AxisType.RIGHT_ROCKER))
            mFlag = true
        } else {
            if (mFlag) {
                mCallback.event(RockerEvent(0, 0, Xbox360Type.Companion.AxisType.RIGHT_ROCKER))
                mFlag = false
            }
        }
    }

    private fun toPercentage(value: Float): Int {
        return (100 * (value / 1.5f)).toInt()
    }

    private fun fixRange(value: Float): Float {
        return when {
            value < -1.5f -> {
                1.5f
            }
            value > 1.5f -> {
                1.5f
            }
            else -> {
                value
            }
        }
    }


    fun initSensorSystem() {
        mSensorManager?.run {
            registerListener(
                mSensorEventListener,
                getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
            registerListener(
                mSensorEventListener,
                getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun disposeSensorSystem() {
        mSensorManager?.run {
            unregisterListener(mSensorEventListener)
        }
    }

}