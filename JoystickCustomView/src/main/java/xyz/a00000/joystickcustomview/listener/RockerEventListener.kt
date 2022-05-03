package xyz.a00000.joystickcustomview.listener

import android.graphics.Point
import android.graphics.PointF
import android.view.MotionEvent
import xyz.a00000.joystickcustomview.view.Rocker
import xyz.a00000.joystickcustomview.bean.KeyEvent
import xyz.a00000.joystickcustomview.bean.RockerEvent
import kotlin.math.abs
import kotlin.math.sqrt

class RockerEventListener(private val mRocker: Rocker) {

    private var mBegin = false
    private var mOutTouchArea = true

    private var mRadius = 0f
    private var mTipsRatio = 0.5f
    private var mClickRatio = 0.2f
    private var mEventType = -1

    fun initData(radius: Float, tipsRatio: Float, clickRatio: Float, eventType: Int) {
        mRadius = radius
        mTipsRatio = tipsRatio
        mClickRatio = clickRatio
        mEventType = eventType
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_MOVE -> onActionMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onActionUpOrCancel(event)
        }
    }

    private fun onActionDown(event: MotionEvent) {
        if (event.x <= mRadius * 2 && event.y <= mRadius * 2) {
            mBegin = true
        }
        if (mBegin) {
            val clickRadius = mRadius * mClickRatio
            val distanceToCenterSquare = abs(event.x - mRadius) * abs(event.x - mRadius) + abs(event.y - mRadius) * abs(event.y - mRadius)
            if (distanceToCenterSquare <= clickRadius * clickRadius) {
                mOutTouchArea = false
            } else {
                mOutTouchArea = true
                mRocker.mCallback?.event(RockerEvent(toPercentage(event.x.toInt()), -toPercentage(event.y.toInt()), mEventType))
                mRocker.changeTipsPos(event.x, event.y)
            }
        }
    }

    private fun onActionMove(event: MotionEvent) {
        if (mBegin) {
            val clickRadius = mRadius * mClickRatio
            val distanceToCenterSquare = abs(event.x - mRadius) * abs(event.x - mRadius) + abs(event.y - mRadius) * abs(event.y - mRadius)
            if (distanceToCenterSquare > clickRadius * clickRadius) {
                mOutTouchArea = true
                if (distanceToCenterSquare <= mRadius * mRadius) {
                    mRocker.mCallback?.event(RockerEvent(toPercentage(event.x.toInt()), -toPercentage(event.y.toInt()), mEventType))
                    mRocker.changeTipsPos(event.x, event.y)
                } else {
                    val scalePoint = scalePoint(PointF(event.x, event.y))
                    mRocker.mCallback?.event(RockerEvent(toPercentage(scalePoint.x.toInt()), -toPercentage(scalePoint.y.toInt()), mEventType))
                    mRocker.changeTipsPos(scalePoint.x, scalePoint.y)
                }
            }
        }
    }

    private fun onActionUpOrCancel(event: MotionEvent) {
        if (mBegin) {
            if (!mOutTouchArea) {
                onClick()
                mOutTouchArea = true
            }
        }
        mRocker. mCallback?.event(RockerEvent(0, 0, mEventType))
        mRocker.changeTipsPos(mRadius, mRadius)
        mBegin = false
    }

    private fun onClick() {
        mRocker.mCallback?.event(KeyEvent(0, mRocker.clickEventType()))
        mRocker.mCallback?.event(KeyEvent(1, mRocker.clickEventType()))
    }

    private fun toPercentage(source: Int): Int {
        return ((source / mRadius) * 100).toInt() - 100
    }

    private fun scalePoint(src: PointF): PointF {
        val ratio = mRadius / sqrt(abs(src.x - mRadius) * abs(src.x - mRadius) + abs(src.y - mRadius) * abs(src.y - mRadius))
        val targetX = (src.x - mRadius) * ratio + mRadius
        val targetY = (src.y - mRadius) * ratio + mRadius
        return PointF(targetX, targetY)
    }

}