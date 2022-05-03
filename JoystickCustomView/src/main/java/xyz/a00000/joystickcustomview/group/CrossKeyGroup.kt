package xyz.a00000.joystickcustomview.group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.view.Callback
import xyz.a00000.joystickcustomview.view.CrossKey
import kotlin.math.min

class CrossKeyGroup(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    private var mContent: View? = null

    init {
        mContent = inflate(getContext(), R.layout.cross_key_group, this)
    }

    fun setCallback(callback: Callback?) {
        mContent?.findViewById<CrossKey>(R.id.ck_top)?.mCallback = callback
        mContent?.findViewById<CrossKey>(R.id.ck_left)?.mCallback = callback
        mContent?.findViewById<CrossKey>(R.id.ck_right)?.mCallback = callback
        mContent?.findViewById<CrossKey>(R.id.ck_bottom)?.mCallback = callback
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val value = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(value, value)
    }

}