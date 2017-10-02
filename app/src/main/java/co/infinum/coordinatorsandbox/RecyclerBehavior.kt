package co.infinum.coordinatorsandbox

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.mcxiaoke.koi.async.koiHandler
import com.mcxiaoke.koi.ext.dpToPx


class RecyclerBehavior(context: Context) : CoordinatorLayout.Behavior<RecyclerView>() {

    val minimumToolbarSize = 56.dpToPx().toFloat()
    var maximumToolbarSize = 180.dpToPx().toFloat()
    var startY = 380.dpToPx().toFloat()

    lateinit var toolbar: View
    lateinit var recycler: RecyclerView
    var isScrolling = false
    val flingRunnable = Runnable { fling() }
    val flinger = Flinger(Scroller(context))
    var initialized = false

    var detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            koiHandler().removeCallbacksAndMessages(null)
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            isScrolling = true
            scroll(distanceY.toInt())
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            flinger.fling(velocityY)
            fling()
            return true
        }
    })

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: RecyclerView, dependency: View?) = R.id.toolbar == dependency?.id

    override fun onMeasureChild(parent: CoordinatorLayout?, child: RecyclerView?, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        if (parent == null || child == null) {
            return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
        }
        val parentHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
        val recyclerHeight = parentHeight - minimumToolbarSize
        val newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(recyclerHeight.toInt(), View.MeasureSpec.EXACTLY)
        parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, newHeightMeasureSpec, heightUsed)
        return true
    }

    override fun onLayoutChild(parent: CoordinatorLayout?, child: RecyclerView?, layoutDirection: Int): Boolean {
        if (parent == null || child == null) {
            return super.onLayoutChild(parent, child, layoutDirection)
        }

        if (initialized.not()) {
            toolbar = parent.getDependencies(child)[0]
            recycler = child
            recycler.y = startY
            initialized = true
        }

        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: RecyclerView?, ev: MotionEvent?): Boolean {
        detector.onTouchEvent(ev)
        return isScrolling
    }

    override fun onTouchEvent(parent: CoordinatorLayout?, child: RecyclerView?, ev: MotionEvent?): Boolean {
        val consumed = detector.onTouchEvent(ev)
        if (ev?.action == MotionEvent.ACTION_UP) {
            isScrolling = false
        }
        return consumed
    }

    private fun fling() {
        val dy = -flinger.getScrollOffsetSinceLastY()
        scroll(dy)
        if (flinger.scroller.isFinished.not()) {
            koiHandler().postDelayed(flingRunnable, 10)
        }
    }

    private fun scroll(dy: Int) {
        val newRecyclerY = if (dy < 0) {
            val verticalOffset = recycler.computeVerticalScrollOffset()
            val unusedDy = if (verticalOffset + dy > 0) 0 else verticalOffset + dy
            Math.min(startY, recycler.y - unusedDy)
        } else {
            Math.max(minimumToolbarSize, recycler.y - dy)
        }

        val translationPercentage = calculateTranslationPercentage(newRecyclerY)
        val newToolbarHeight = calculateToolbarHeight(translationPercentage)

        val layoutParams = toolbar.layoutParams
        layoutParams.height = newToolbarHeight
        toolbar.layoutParams = layoutParams

        val usedScroll = recycler.y - newRecyclerY
        toolbar.y = newRecyclerY - newToolbarHeight
        recycler.y = newRecyclerY

        val leftDistance = (dy - usedScroll).toInt()
        recycler.scrollBy(0, leftDistance)
    }

    private fun calculateToolbarHeight(translationPercentage: Float): Int {
        val heightDifference = maximumToolbarSize - minimumToolbarSize
        return (heightDifference * translationPercentage + minimumToolbarSize).toInt()
    }

    fun calculateTranslationPercentage(newRecyclerY: Float): Float {
        val totalTranslation = startY - minimumToolbarSize
        val currentTranslation = newRecyclerY - minimumToolbarSize
        return currentTranslation / totalTranslation
    }

    /**
     * Simple wrapper class that calculates offset from last known position.
     */
    class Flinger(val scroller: Scroller) {

        var previousY: Int = 0

        fun fling(velocityY: Float) {
            previousY = 0
            scroller.fling(0, 0, 0, velocityY.toInt(), 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        }

        fun getScrollOffsetSinceLastY(): Int {
            scroller.computeScrollOffset()
            val newY = scroller.currY
            val diff = newY - previousY
            previousY = newY
            return diff
        }
    }
}