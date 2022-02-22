package xyz.a00000.gamewirelesscontroller.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.gamewirelesscontroller.R
import kotlin.math.abs

open class SimpleButton(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    companion object {

        @JvmStatic
        val DEFAULT_RADIUS = 100
        @JvmStatic
        val DEFAULT_BACKGROUND_COLOR = Color.GRAY
        @JvmStatic
        val DEFAULT_FONT_COLOR = Color.BLACK

    }

    val mPaint = Paint()
    var mRadius = DEFAULT_RADIUS
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var mBackgroundColor = DEFAULT_BACKGROUND_COLOR
        set(value) {
            field = value
            invalidate()
        }
    var mFontColor = DEFAULT_FONT_COLOR
        set(value) {
            field = value
            invalidate()
        }

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.SimpleButton)
        mRadius = arr.getInt(R.styleable.SimpleButton_radius_2, DEFAULT_RADIUS)
        mBackgroundColor = arr.getColor(R.styleable.SimpleButton_backgroundColor, DEFAULT_BACKGROUND_COLOR)
        mFontColor = arr.getColor(R.styleable.SimpleButton_fontColor_2, DEFAULT_FONT_COLOR)
        arr.recycle()
    }

    var mCallback: Callback? = null

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mPaint.isAntiAlias = true
            mPaint.color = mBackgroundColor
            it.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mPaint)
            mPaint.color = mFontColor
            mPaint.textSize = textSize
            it.translate(width.toFloat() / 2, height.toFloat() / 2)
            val textWidth = mPaint.measureText(text.toString())
            val baseLineY = abs(mPaint.ascent() + mPaint.descent()) / 2
            it.drawText(text.toString(), -textWidth / 2, baseLineY, mPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mRadius * 2, mRadius * 2)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    swapColor()
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    swapColor()
                }
            }
            mCallback?.press(event.action)
            if (it.action == MotionEvent.ACTION_UP || it.action == MotionEvent.ACTION_CANCEL) {
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    interface Callback {
        fun press(action: Int)
    }


    fun swapColor() {
        val tmp = mBackgroundColor
        mBackgroundColor = mFontColor
        mFontColor = tmp
    }

}