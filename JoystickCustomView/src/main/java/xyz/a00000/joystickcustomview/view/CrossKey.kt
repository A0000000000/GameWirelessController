package xyz.a00000.joystickcustomview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import xyz.a00000.joystickcustomview.R
import xyz.a00000.joystickcustomview.bean.KeyEvent
import kotlin.math.abs
import kotlin.math.min

class CrossKey(context: Context, attrs: AttributeSet): AppCompatTextView(context, attrs) {

    private val mPaint = Paint()
    private val mRect = Rect(0, 0, 0, 0)

    private var mType = -1
    private var mBackColor = Color.GRAY
    private var mFontColor = Color.BLACK

    var mCallback: Callback? = null

    init {
        isClickable = true
        val arr = context.obtainStyledAttributes(attrs, R.styleable.CrossKey)
        mType = arr.getInt(R.styleable.CrossKey_ck_type, -1)
        mBackColor = arr.getColor(R.styleable.CrossKey_ck_back_color, Color.GRAY)
        mFontColor =  arr.getColor(R.styleable.CrossKey_ck_front_color, Color.BLACK)
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
        mRect.right = measuredWidth
        mRect.bottom = measuredHeight
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            mPaint.color = mBackColor
            drawRect(mRect, mPaint)
            mPaint.color = mFontColor
            mPaint.textSize = textSize
            translate((mRect.right / 2).toFloat(), (mRect.bottom / 2).toFloat())
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