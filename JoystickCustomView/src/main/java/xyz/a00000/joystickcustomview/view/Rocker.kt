package xyz.a00000.joystickcustomview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.Xbox360Type
import xyz.a00000.joystickcustomview.listener.RockerEventListener
import kotlin.math.min

class Rocker(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val mRockerEventListener = RockerEventListener(this)
    private val mTipsRatio = 0.5f
    private val mClickRatio = 0.2f
    private val mPaint = Paint()
    private val mBackCenter = PointF(0f, 0f)
    private val mFrontCenter = PointF(0f, 0f)
    private var mRadius = 0f

    private var mBackColor = Color.GRAY
    private var mFrontColor = Color.WHITE
    private var mClickColor = Color.BLACK
    private var mType = -1

    var mCallback: Callback? = null

    init {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.Rocker)
        mBackColor = arr.getColor(R.styleable.Rocker_r_back_color, Color.GRAY)
        mFrontColor = arr.getColor(R.styleable.Rocker_r_front_color, Color.WHITE)
        mClickColor = arr.getColor(R.styleable.Rocker_r_click_color, Color.BLACK)
        mType = arr.getInt(R.styleable.Rocker_r_type, -1)
        arr.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            mRockerEventListener.onTouchEvent(it)
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val value = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(value, value)
        mRadius = min(measuredHeight, measuredWidth) / 2f
        mBackCenter.x = mRadius
        mBackCenter.y = mRadius
        mFrontCenter.x = mRadius
        mFrontCenter.y = mRadius
        mRockerEventListener.initData(mRadius, mTipsRatio, mClickRatio, mType)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            mPaint.isAntiAlias = true
            mPaint.color = mBackColor
            drawCircle(mBackCenter.x, mBackCenter.y, mRadius, mPaint)
            mPaint.color = mFrontColor
            drawCircle(mFrontCenter.x, mFrontCenter.y, mRadius * mTipsRatio, mPaint)
            mPaint.color = mClickColor
            drawCircle(mBackCenter.x, mBackCenter.y, mRadius * mClickRatio, mPaint)
        }
    }

    fun changeTipsPos(x: Float, y: Float) {
        mFrontCenter.x = x
        mFrontCenter.y = y
        invalidate()
    }

    fun clickEventType(): Int {
        return when (mType) {
            Xbox360Type.Companion.AxisType.LEFT_ROCKER -> Xbox360Type.Companion.KeyType.LEFT_ROCKER
            Xbox360Type.Companion.AxisType.RIGHT_ROCKER -> Xbox360Type.Companion.KeyType.RIGHT_ROCKER
            else -> -1
        }
    }

}