package xyz.a00000.joystickcustomview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.TriggerEvent
import kotlin.math.abs

class Trigger(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    private val mPaint = Paint()
    private val mRect = Rect(0, 0, 0, 0)
    private val mFrontRect = Rect(0, 0, 0, 0)

    private var mBackColor = Color.GRAY
    private var mFrontColor = Color.WHITE
    private var mFontColor = Color.BLACK
    private var mBeginToEnd = true
    private var mType = -1
    private var mFlag = true

    var mCallback: Callback? = null

    init {
        isClickable = true
        setTextColor(Color.BLACK)
        val arr = context.obtainStyledAttributes(attrs, R.styleable.Trigger)
        mBackColor = arr.getColor(R.styleable.Trigger_t_back_color, Color.GRAY)
        mFrontColor = arr.getColor(R.styleable.Trigger_t_front_color, Color.WHITE)
        mFontColor = arr.getColor(R.styleable.Trigger_t_font_color, Color.BLACK)
        mBeginToEnd = arr.getBoolean(R.styleable.Trigger_t_begin_to_end, true)
        mType = arr.getInt(R.styleable.Trigger_t_type, -1)
        arr.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRect.right = measuredWidth
        mRect.bottom = measuredHeight
        mFlag = mRect.right > mRect.bottom
        if (mFlag) {
            mFrontRect.set(0, 0, 0, mRect.bottom)
        } else {
            mFrontRect.set(0, 0, mRect.right, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            mPaint.color = mBackColor
            drawRect(mRect, mPaint)
            mPaint.color = mFrontColor
            drawRect(mFrontRect, mPaint)
            mPaint.color = mFontColor
            mPaint.textSize = textSize
            translate(measuredWidth / 2f, measuredHeight / 2f)
            val textWidth = mPaint.measureText(text.toString())
            val baseLineY = abs(mPaint.ascent() + mPaint.descent()) / 2
            drawText(text.toString(), -textWidth / 2, baseLineY, mPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var ratio = if (mFlag) {
                        x / mRect.right
                    } else {
                        y / mRect.bottom
                    }
                    if (ratio < 0f) {
                        ratio = 0f
                    }
                    if (ratio > 1f) {
                        ratio = 1f
                    }
                    if (mBeginToEnd) {
                        if (mFlag) {
                            mFrontRect.right = (mRect.right * ratio).toInt()
                        } else {
                            mFrontRect.bottom = (mRect.bottom * ratio).toInt()
                        }
                        mCallback?.event(TriggerEvent((100 * ratio).toInt(), mType))
                    } else {
                        if (mFlag) {
                            mFrontRect.left = (mRect.right * ratio).toInt()
                            mFrontRect.right = mRect.right
                        } else {
                            mFrontRect.top = (mRect.bottom * ratio).toInt()
                            mFrontRect.bottom = mRect.bottom
                        }

                        mCallback?.event(TriggerEvent((100 * (1 - ratio)).toInt(), mType))
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (mFlag) {
                        mFrontRect.set(0, 0, 0, mRect.bottom)
                    } else {
                        mFrontRect.set(0, 0, mRect.right, 0)
                    }
                    mCallback?.event(TriggerEvent(0, mType))
                }
            }
            invalidate()
        }
        return true
    }


}