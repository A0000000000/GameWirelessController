package xyz.a00000.joystickcustomview.group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.view.Callback
import xyz.a00000.joystickcustomview.view.SimpleKey
import kotlin.math.min

class OperatorPanelGroup(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    private var mContent: View? = null

    init {
        mContent = inflate(getContext(), R.layout.operator_panel_group, this)
    }

    fun setCallback(callback: Callback?) {
        mContent?.findViewById<SimpleKey>(R.id.sk_main)?.mCallback = callback
        mContent?.findViewById<SimpleKey>(R.id.sk_view)?.mCallback = callback
        mContent?.findViewById<SimpleKey>(R.id.sk_menu)?.mCallback = callback
        mContent?.findViewById<SimpleKey>(R.id.sk_function)?.mCallback = callback
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val value = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(value, value)
    }

}