package xyz.a00000.joystickcustomview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.GameEvent

open class CrossKeyView(context: Context, attrs: AttributeSet): View(context, attrs) {

    companion object {
        @JvmStatic
        val DEFAULT_LONG = 100
        @JvmStatic
        val DEFAULT_WIDE = 100
        @JvmStatic
        val COLOR = Color.GRAY
        @JvmStatic
        val CLICK_COLOR = Color.WHITE
    }

    var mPaint = Paint()

    var mLong = DEFAULT_LONG
        set(value) {
            field = value
            requestLayout()
        }
    var mWide = DEFAULT_WIDE
        set(value) {
            field = value
            requestLayout()
        }

    var mLandscape = true
        set(value) {
            field = value
            requestLayout()
        }

    var mColor = Color.GRAY

    var mType = -1

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.CrossKeyView)
        mLandscape = arr.getBoolean(R.styleable.CrossKeyView_ckv_landscape, true)
        mLong = arr.getInt(R.styleable.CrossKeyView_ckv_long, DEFAULT_LONG)
        mWide = arr.getInt(R.styleable.CrossKeyView_ckv_wide, DEFAULT_WIDE)
        mType = arr.getInt(R.styleable.CrossKeyView_ckv_type, -1)
        arr.recycle()
    }

    var mCallback: Callback? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mLandscape) {
            setMeasuredDimension(mLong, mWide)
        } else {
            setMeasuredDimension(mWide, mLong)
        }
    }

    private val mDrawRect = Rect()

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mPaint.color = mColor
            if (mLandscape) {
                mDrawRect.set(0, 0, mLong, mWide)
            } else {
                mDrawRect.set(0, 0, mWide, mLong)
            }
            canvas.drawRect(mDrawRect, mPaint)
        }
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mColor = CLICK_COLOR
                    invalidate()
                    mCallback?.event(GameEvent.createKeyEvent(mType, 0))
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mColor = COLOR
                    invalidate()
                    mCallback?.event(GameEvent.createKeyEvent(mType, 1))
                }
                else -> {

                }
            }
        }
        return super.onTouchEvent(event)
    }


}