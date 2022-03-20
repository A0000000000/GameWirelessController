package xyz.a00000.joystickcustomview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.GameEvent
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
    var mType = -1

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.SimpleButton)
        mRadius = arr.getInt(R.styleable.SimpleButton_sb_radius, DEFAULT_RADIUS)
        mBackgroundColor = arr.getColor(R.styleable.SimpleButton_sb_background_color, DEFAULT_BACKGROUND_COLOR)
        mFontColor = arr.getColor(R.styleable.SimpleButton_sb_font_color, DEFAULT_FONT_COLOR)
        mType = arr.getInt(R.styleable.SimpleButton_sb_type, -1)
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    swapColor()
                    mCallback?.event(GameEvent.createKeyEvent(mType, 0))
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mCallback?.event(GameEvent.createKeyEvent(mType, 1))
                    swapColor()
                }
                else -> {

                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun swapColor() {
        val tmp = mBackgroundColor
        mBackgroundColor = mFontColor
        mFontColor = tmp
    }

}