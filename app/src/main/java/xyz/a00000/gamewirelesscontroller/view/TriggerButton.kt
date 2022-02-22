package xyz.a00000.gamewirelesscontroller.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.gamewirelesscontroller.R
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.max

open class TriggerButton(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    companion object {

        @JvmStatic
        val DEFAULT_LENGTH = 300
        @JvmStatic
        val DEFAULT_HIGH = 100
        @JvmStatic
        val DEFAULT_BACKGROUND_COLOR = Color.GRAY
        @JvmStatic
        val DEFAULT_FRONT_COLOR = Color.WHITE

    }

    var mDirection = true
    var mLength = DEFAULT_LENGTH
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var mHigh = DEFAULT_HIGH
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var mCurrent = 0f
        private set(value) {
            field = value
            invalidate()
        }
    var mBackgroundColor = DEFAULT_BACKGROUND_COLOR
    var mFrontColor = DEFAULT_FRONT_COLOR
    var mFontColor = Color.BLACK

    var mPaint = Paint()

    var mCallback: Callback? = null


    private val mBackgroundRect = Rect()
    private val mPressRect = Rect()

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.TriggerButton)
        mLength = arr.getInt(R.styleable.TriggerButton_length, DEFAULT_LENGTH)
        mHigh = arr.getInt(R.styleable.TriggerButton_high, DEFAULT_HIGH)
        mFontColor = arr.getColor(R.styleable.TriggerButton_fontColor, Color.BLACK)
        mDirection = arr.getBoolean(R.styleable.TriggerButton_direction, true)
        arr.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mPaint.color = mBackgroundColor
            mBackgroundRect.set(0, 0, mLength, mHigh)
            it.drawRect(mBackgroundRect, mPaint)
            mPaint.color = mFrontColor
            if (mDirection) {
                mPressRect.set(0, 0, (mLength * mCurrent).toInt(), mHigh)
            } else {
                mPressRect.set((mLength * (1 - mCurrent)).toInt(), 0, mLength, mHigh)
            }
            it.drawRect(mPressRect, mPaint)
            mPaint.color = mFontColor
            mPaint.textSize = textSize
            it.translate(width.toFloat() / 2, height.toFloat() / 2)
            val textWidth = mPaint.measureText(text.toString())
            val baseLineY = abs(mPaint.ascent() + mPaint.descent()) / 2
            it.drawText(text.toString(), -textWidth / 2, baseLineY, mPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mLength, mHigh)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    val ratio = it.x / mLength
                    mCurrent = if (mDirection) ratio else 1 - ratio
                }
                MotionEvent.ACTION_MOVE -> {
                    val ratio = it.x / mLength
                    mCurrent = if (mDirection) ratio else 1 - ratio
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mCurrent = 0f
                }
            }
            mCallback?.press(max(min((100 * mCurrent).toInt(), 100), 0), it.action)
            if (it.action == MotionEvent.ACTION_UP || it.action == MotionEvent.ACTION_CANCEL) {
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    interface Callback {
        fun press(trigger: Int, action: Int)
    }

}