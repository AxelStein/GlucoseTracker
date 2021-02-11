package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.absoluteValue

class LineChartView : View, GestureDetector.OnGestureListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var data = listOf(
        PointF(0f, 4f), PointF(1f, 3f), PointF(3f, 5f), PointF(5f, 2f),
        PointF(6f, 6f), PointF(7f, 7.5f), PointF(8f, 3.5f), PointF(9f, 2.5f),
        PointF(10f, 4.5f), PointF(11f, 5f), PointF(12f, 4.5f), PointF(13f, 7f),
        PointF(14f, 4f), PointF(15f, 3f), PointF(16f, 5f), PointF(17f, 2f),
        PointF(18f, 6f), PointF(19f, 7.5f), PointF(20f, 3.5f), PointF(22f, 2.5f),
        PointF(23f, 4.5f), PointF(25f, 5f), PointF(26f, 4.5f), PointF(27f, 7f),
    )
    private val labels = listOf("Dec", "2", "3", "4", "5", "6")
    private val renderer = LineChartRenderer(context)
    private val gestureDetector = GestureDetectorCompat(context, this)

    init {
        setData(data)
    }

    fun setData(data: List<PointF>) {
        this.data = data
        /*var maxValue = 0f
        data.forEach { point ->
            if (point.y >= maxValue) {
                maxValue = point.y
            }
        }
        if (maxValue.rem(2f) == 1f) {
            maxValue += 1f
        }*/
        renderer.setMaxY(10f)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRGB(255, 255, 255)
        renderer.drawBorders(canvas)
        renderer.drawVerticalGridLines(canvas)
        renderer.drawHorizontalGridLines(canvas)
        renderer.drawChart(canvas, data)
        renderer.drawHorizontalLabels(canvas, labels)
        renderer.drawVerticalLabels(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val defaultPadding = 16f.intoPx(context)
        /*val m = fontPaint.measureText(renderer.getMaxY().toInt().toString())
        var paddingLeft = m * 3f
        if (paddingLeft < defaultPadding) paddingLeft = defaultPadding
        val paddingBottom = defaultPadding*/
        renderer.setChartBorders(defaultPadding, defaultPadding, w - defaultPadding, h - defaultPadding)
    }

    override fun onDown(e: MotionEvent?) = true

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?) = true

    var totalDistanceX = 0f
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        totalDistanceX += distanceX
        if (totalDistanceX.absoluteValue >= (renderer.getDx() / 2)) {
            renderer.move(distanceX)
            totalDistanceX = 0f
            invalidate()
            // return true
        }
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}