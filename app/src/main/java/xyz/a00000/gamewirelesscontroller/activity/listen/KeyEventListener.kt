package xyz.a00000.gamewirelesscontroller.activity.listen

import android.view.KeyEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.view.Callback

class KeyEventListener(private val mCallback: Callback) {

    private var mFirstVolumeDown = true

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
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
        return false
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
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
        return false
    }



}