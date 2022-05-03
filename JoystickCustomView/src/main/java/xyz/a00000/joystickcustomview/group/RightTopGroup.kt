package xyz.a00000.joystickcustomview.group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.view.Callback
import xyz.a00000.joystickcustomview.view.SimpleKey
import xyz.a00000.joystickcustomview.view.Trigger

class RightTopGroup(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    private var mContent: View? = null

    init {
        mContent = inflate(getContext(), R.layout.right_top_group, this)
    }

    fun setCallback(callback: Callback?) {
        mContent?.findViewById<SimpleKey>(R.id.sk_rb)?.mCallback = callback
        mContent?.findViewById<Trigger>(R.id.t_rt)?.mCallback = callback
    }

}