package xyz.a00000.gamewirelesscontroller.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.a00000.gamewirelesscontroller.R
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.sqrt

open class RockerView(context: Context, attrs: AttributeSet): View(context, attrs) {

    companion object {

        @JvmStatic
        var DEFAULT_BACK_COLOR: Int = Color.GRAY
        @JvmStatic
        var DEFAULT_FRONT_COLOR: Int = Color.WHITE
        @JvmStatic
        var DEFAULT_RADIUS: Float = 200f
        @JvmStatic
        var DEFAULT_RATIO: Float = 0.5f

    }

    var mBackColor: Int = DEFAULT_BACK_COLOR
    var mFrontColor: Int = DEFAULT_FRONT_COLOR
    var mRadius: Float = DEFAULT_RADIUS
        set(value) {
            field = value
            mBackPoint.set(mRadius.toInt(), mRadius.toInt())
            mFrontPoint.set(mRadius.toInt(), mRadius.toInt())
            requestLayout()
            invalidate()
        }
    var mRatio: Float = DEFAULT_RATIO
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var mPaint: Paint = Paint()
    var mBackPoint: Point = Point(mRadius.toInt(), mRadius.toInt())
        get() = Point(mRadius.toInt(), mRadius.toInt())
        private set
    var mFrontPoint: Point = Point(mRadius.toInt(), mRadius.toInt())

    var mTouchCenterRation: Float = 0.2f
    var mTouchOutArea: Boolean = false

    var mCallback: Callback? = null

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.RockerView)
        mRadius = arr.getFloat(R.styleable.RockerView_radius, DEFAULT_RADIUS)
        arr.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            mPaint.isAntiAlias = true
            mPaint.color = mBackColor
            it.drawCircle(mBackPoint.x.toFloat(), mBackPoint.y.toFloat(), mRadius, mPaint)
            mPaint.color = mFrontColor
            it.drawCircle(mFrontPoint.x.toFloat(), mFrontPoint.y.toFloat(), mRadius * mRatio, mPaint)
        }

        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((mRadius * 2).toInt(), (mRadius * 2).toInt())
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val subX = abs(mBackPoint.x - event.x)
            val subY = abs(mBackPoint.y - event.y)
            val distance = subX * subX + subY * subY
            if (distance <= mRadius * mRadius) {
                mCallback?.let { call ->
                    var x: Int = (100 * (event.x - mRadius) / mRadius).toInt()
                    var y: Int = (100 * (mRadius - event.y) / mRadius).toInt()
                    x = max(-100, x)
                    x = min(100, x)
                    y = max(-100, y)
                    y = min(100, y)
                    call.move(x, y, event.action)
                }
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (distance > mRadius * mRadius * mTouchCenterRation * mTouchCenterRation) {
                            mTouchOutArea = true
                        }
                        if (!mTouchOutArea) {
                            mCallback?.click(event.action)
                        }
                        mFrontPoint.x = event.x.toInt()
                        mFrontPoint.y = event.y.toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (!mTouchOutArea) {
                            mCallback?.click(event.action)
                        } else {
                            mCallback?.click(MotionEvent.ACTION_UP)
                        }
                        mFrontPoint.x = event.x.toInt()
                        mFrontPoint.y = event.y.toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        if (!mTouchOutArea) {
                            mCallback?.click(event.action)
                        }
                        mFrontPoint.x = mBackPoint.x
                        mFrontPoint.y = mBackPoint.y
                        mTouchOutArea = false
                        invalidate()
                    }
                }
            } else {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        var x = event.x - mRadius
                        var y = mRadius - event.y
                        val ratio = sqrt(distance / (mRadius * mRadius))
                        x /= ratio
                        y /= ratio
                        mCallback?.let { call ->
                            var ex: Int = (100 * x / mRadius).toInt()
                            var ey: Int = (100 * y / mRadius).toInt()
                            ex = max(-100, ex)
                            ex = min(100, ex)
                            ey = max(-100, ey)
                            ey = min(100, ey)
                            call.move(ex, ey, event.action)
                        }
                        mFrontPoint.x = (x + mRadius).toInt()
                        mFrontPoint.y = (mRadius - y).toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        mFrontPoint.x = mBackPoint.x
                        mFrontPoint.y = mBackPoint.y
                        mTouchOutArea = false
                        var x = event.x - mRadius
                        var y = mRadius - event.y
                        val ratio = sqrt(distance / (mRadius * mRadius))
                        x /= ratio
                        y /= ratio
                        mCallback?.let { call ->
                            var ex: Int = (100 * x / mRadius).toInt()
                            var ey: Int = (100 * y / mRadius).toInt()
                            ex = max(-100, ex)
                            ex = min(100, ex)
                            ey = max(-100, ey)
                            ey = min(100, ey)
                            call.move(ex, ey, event.action)
                        }
                        invalidate()
                    }
                }
            }
            if (it.action == MotionEvent.ACTION_UP || it.action == MotionEvent.ACTION_CANCEL) {
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    interface Callback {
        fun click(action: Int)
        fun move(x: Int, y: Int, action: Int)
    }

}