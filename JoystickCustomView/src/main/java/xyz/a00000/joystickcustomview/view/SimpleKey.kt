package xyz.a00000.joystickcustomview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.KeyEvent
import kotlin.math.abs
import kotlin.math.min

class SimpleKey(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    private val mPaint = Paint()
    private var mRadius = 0f

    var mCallback: Callback? = null

    private var mBackColor = Color.GRAY
    private var mFontColor = Color.BLACK
    private var mType = -1

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.SimpleKey)
        mType = arr.getInt(R.styleable.SimpleKey_sk_type, -1)
        mBackColor = arr.getColor(R.styleable.SimpleKey_sk_back_color, Color.GRAY)
        mFontColor =  arr.getColor(R.styleable.SimpleKey_sk_font_color, Color.BLACK)
        arr.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            if (mType != -1) {
                when(action) {
                    MotionEvent.ACTION_DOWN -> {
                        mCallback?.event(KeyEvent(0, mType))
                        swapColor()
                        return@onTouchEvent true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        mCallback?.event(KeyEvent(1, mType))
                        swapColor()
                        return@onTouchEvent true
                    }
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val value = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(value, value)
        mRadius = min(measuredHeight, measuredWidth) / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            mPaint.color = mBackColor
            drawCircle(mRadius, mRadius, mRadius, mPaint)
            mPaint.color = mFontColor
            mPaint.textSize = textSize
            translate(mRadius, mRadius)
            val textWidth = mPaint.measureText(text.toString())
            val baseLineY = abs(mPaint.ascent() + mPaint.descent()) / 2
            drawText(text.toString(), -textWidth / 2, baseLineY, mPaint)
        }
    }

    private fun swapColor() {
        val tmp = mFontColor
        mFontColor = mBackColor
        mBackColor = tmp
        invalidate()
    }

}