package xyz.a00000.joystickcustomview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.GameEvent
import xyz.a00000.joystickcustomview.bean.Xbox360Type
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
    var mIsTouch: Boolean = false

    var mCallback: Callback? = null
    var mType = -1
    var mKeyType = -1
        get() {
            if (mType == Xbox360Type.Companion.AxisType.LEFT_ROCKER) {
                return Xbox360Type.Companion.KeyType.LEFT_ROCKER
            }
            if (mType == Xbox360Type.Companion.AxisType.RIGHT_ROCKER) {
                return Xbox360Type.Companion.KeyType.RIGHT_ROCKER
            }
            return -1
        }

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.RockerView)
        mRadius = arr.getFloat(R.styleable.RockerView_rv_radius, DEFAULT_RADIUS)
        mType = arr.getInt(R.styleable.RockerView_rv_type, -1)
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val subX = abs(mBackPoint.x - it.x)
            val subY = abs(mBackPoint.y - it.y)
            val distance = subX * subX + subY * subY
            if (distance <= mRadius * mRadius) {
                mCallback?.let { call ->
                    var x: Int = (100 * (it.x - mRadius) / mRadius).toInt()
                    var y: Int = (100 * (mRadius - it.y) / mRadius).toInt()
                    x = max(-100, x)
                    x = min(100, x)
                    y = max(-100, y)
                    y = min(100, y)
                    call.event(GameEvent.createRockerEvent(mType, x, y))
                }
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (distance < mRadius * mRadius * mTouchCenterRation * mTouchCenterRation) {
                            mIsTouch = true
                            mCallback?.event(GameEvent.createKeyEvent(mKeyType, 0))
                        }
                        mFrontPoint.x = it.x.toInt()
                        mFrontPoint.y = it.y.toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (mIsTouch && distance > mRadius * mRadius * mTouchCenterRation * mTouchCenterRation) {
                            mIsTouch = false
                            mCallback?.event(GameEvent.createKeyEvent(mKeyType, 1))
                        }
                        mFrontPoint.x = it.x.toInt()
                        mFrontPoint.y = it.y.toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        if (mIsTouch && distance < mRadius * mRadius * mTouchCenterRation * mTouchCenterRation) {
                            mIsTouch = false
                            mCallback?.event(GameEvent.createKeyEvent(mKeyType, 1))
                        }
                        mFrontPoint.x = mBackPoint.x
                        mFrontPoint.y = mBackPoint.y
                        mIsTouch = false
                        mCallback?.event(GameEvent.createRockerEvent(mType, 0, 0))
                        invalidate()
                    }
                }
            } else {
                when (it.action) {
                    MotionEvent.ACTION_MOVE -> {
                        var x = it.x - mRadius
                        var y = mRadius - it.y
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
                            call.event(GameEvent.createRockerEvent(mType, ex, ey))
                        }
                        mFrontPoint.x = (x + mRadius).toInt()
                        mFrontPoint.y = (mRadius - y).toInt()
                        invalidate()
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        mFrontPoint.x = mBackPoint.x
                        mFrontPoint.y = mBackPoint.y
                        mCallback?.event(GameEvent.createRockerEvent(mType, 0, 0))
                        invalidate()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

}