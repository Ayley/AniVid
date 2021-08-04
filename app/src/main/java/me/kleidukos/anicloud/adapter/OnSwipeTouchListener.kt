package me.kleidukos.anicloud.adapter

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector = GestureDetector(ctx, GestureListener())

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener() :
        GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return e1?.action == MotionEvent.ACTION_DOWN && e2?.action == MotionEvent.ACTION_UP
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {

            val diffY = e2!!.y - e1!!.y
            if (abs(diffY) > SWIPE_THRESHOLD) {
                if (distanceY.toInt() % 2 == 0) {
                    if (distanceY > 0) {
                        onSwipeTop(e2!!)
                    } else {
                        onSwipeBottom(e2!!)
                    }
                }
            }
            return false
        }
    }

    open fun onSwipeRight(event: MotionEvent) {}

    open fun onSwipeLeft(event: MotionEvent) {}

    open fun onSwipeTop(event: MotionEvent) {}

    open fun onSwipeBottom(event: MotionEvent) {}
}