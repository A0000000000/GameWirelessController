package xyz.a00000.joystickcustomview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.RockerEvent

class AxisView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val mPaint = Paint()
    private val mCenter = PointF(0f, 0f);
    private val mRect = RectF(0f, 0f, 0f, 0f)

    private var mHorizontal = true
    private var mHalfLength = 0f
    private var mRadius = 0f

    private var mBackColor = Color.GRAY
    private var mFrontColor = Color.WHITE
    private var mType = -1

    var mCallback: Callback? = null

    init {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AxisView)
        mBackColor = arr.getColor(R.styleable.AxisView_av_back_color, Color.GRAY)
        mFrontColor = arr.getColor(R.styleable.AxisView_av_front_color, Color.WHITE)
        mType = arr.getInt(R.styleable.AxisView_av_type, -1)
        arr.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            var value = 0
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    if (mHorizontal) {
                        mCenter.y = y
                        value = (100 * ((y / mHalfLength) - 1)).toInt()
                    } else {
                        mCenter.x = x
                        value = (100 * ((x / mHalfLength) - 1)).toInt()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    value = 0
                    if (mHorizontal) {
                        mCenter.y = measuredHeight / 2f
                    } else {
                        mCenter.x = measuredWidth / 2f
                    }
                }
                else -> {
                    value = 0
                    if (mHorizontal) {
                        mCenter.y = measuredHeight / 2f
                    } else {
                        mCenter.x = measuredWidth / 2f
                    }
                }
            }
            if (value < -100) {
                value = -100
            }
            if (value > 100) {
                value = 100
            }
            if (mHorizontal) {
                if (mCenter.y < 0) {
                    mCenter.y = 0f
                }
                if (mCenter.y > measuredHeight){
                    mCenter.y = measuredHeight.toFloat()
                }
                mCallback?.event(RockerEvent(0, -value, mType))
            } else {
                if (mCenter.x < 0) {
                    mCenter.x = 0f
                }
                if (mCenter.x > measuredWidth){
                    mCenter.x = measuredWidth.toFloat()
                }
                mCallback?.event(RockerEvent(value, 0, mType))
            }
        }
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHorizontal = measuredWidth < measuredHeight
        mCenter.x = measuredWidth / 2f
        mCenter.y = measuredHeight / 2f
        if (mHorizontal) {
            mRect.left = measuredWidth / 3f
            mRect.right = 2 * measuredWidth / 3f
            mRect.top = 0f
            mRect.bottom = measuredHeight.toFloat()
            mRadius = measuredWidth / 2f
            mHalfLength = measuredHeight / 2f
        } else {
            mRect.left = 0f
            mRect.right = measuredWidth.toFloat()
            mRect.top = measuredHeight / 3f
            mRect.bottom = 2 * measuredHeight / 3f
            mHalfLength = measuredWidth / 2f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            mPaint.color = mBackColor
            drawRect(mRect, mPaint)
            mPaint.color = mFrontColor
            drawCircle(mCenter.x, mCenter.y, mRadius, mPaint)
        }
    }

}