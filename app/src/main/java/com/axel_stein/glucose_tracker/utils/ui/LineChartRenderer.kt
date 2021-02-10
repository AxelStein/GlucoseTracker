package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.graphics.*

class LineChartRenderer(private val context: Context) {
    private var chartWidth = 0f
    private var chartHeight = 0f
    private var dx = 0f
    private var dy = 0f
    private var circleRadius = 3f.intoPx(context)
    private var lineWidth = 1.5f.intoPx(context)
    private var index = 0
    private var minY = 0f
    private var maxY = 10f
    private var dataCount = 0
    private var xRange = 10
    private var chartRect = RectF()
    private val path = Path()
    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        isAntiAlias = true
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val linePaint = Paint().apply {
        color = Color.parseColor("#1976D2")  // #00796B
        isAntiAlias = true
        strokeWidth = lineWidth
    }
    private val fontPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        textSize = 10f.intoPx(context)
    }

    fun setChartBorders(left: Float, top: Float, right: Float, bottom: Float) {
        chartRect = RectF(left, top, right, bottom)

        chartWidth = chartRect.right - chartRect.left
        chartHeight = chartRect.bottom - chartRect.top

        dx = chartWidth.div(xRange)
        dy = chartHeight.div(maxY)
    }

    fun setXRange(max: Int) {
        this.xRange = max
    }

    fun setMaxY(max: Float) {
        maxY = max
    }

    fun getMaxY() = maxY

    fun setCircleRadius(radius: Float) {
        circleRadius = radius.intoPx(context)
    }

    fun setLineWidth(width: Float) {
        lineWidth = width.intoPx(context)
        linePaint.strokeWidth = lineWidth
    }

    fun getDx() = dx

    fun move(dx: Float) {
        if (dx < 0) { // to the left
            index--
            if (index < 0) index = 0
        } else { // to the right
            if (index < dataCount - xRange - 1) index++
        }
    }

    fun drawBorders(canvas: Canvas) {
        path.reset()
        path.moveTo(chartRect.left, chartRect.top)
        path.lineTo(chartRect.right, chartRect.top)
        path.lineTo(chartRect.right, chartRect.bottom)
        path.lineTo(chartRect.left, chartRect.bottom)
        path.lineTo(chartRect.left, chartRect.top)
        canvas.drawPath(path, gridPaint)
    }

    fun drawHorizontalGridLines(canvas: Canvas) {
        val lineCount = maxY.div(2).toInt()
        canvas.save()
        canvas.translate(chartRect.left, chartRect.top)
        for (i in 0 until lineCount) {
            canvas.translate(0f, dy * 2)
            canvas.drawLine(0f, 0f, chartWidth, 0f, gridPaint)
        }
        canvas.restore()
    }

    fun drawVerticalGridLines(canvas: Canvas) {
        canvas.save()
        canvas.translate(chartRect.left, chartRect.top)
        for (i in 0 until xRange) {
            canvas.drawLine(0f, 0f, 0f, chartHeight, gridPaint)
            canvas.translate(dx, 0f)
        }
        canvas.restore()
    }

    fun drawChart(canvas: Canvas, data: List<PointF>) {
        dataCount = data.size

        var prevX = 0f
        var prevY = 0f

        canvas.save()
        canvas.translate(chartRect.left, chartRect.top)

        var j = 0
        for (i in index .. (index + xRange)) {
            if (i >= 0 && i < data.size) {
                val point = data[i]
                val y = point.y.times(chartHeight).div(maxY)
                val cy = chartHeight.minus(y)
                val cx = dx.times(j)
                canvas.drawCircle(cx, cy, circleRadius, linePaint)
                if (j > 0) {
                    canvas.drawLine(prevX, prevY, cx, cy, linePaint)
                }
                j++
                prevX = cx
                prevY = cy
                // if (i == data.size-1) break
            }
        }
        /*for (point in data) {
            // convert point y to chart y
            val y = point.y.times(chartHeight).div(maxY)
            val cy = chartHeight.minus(y)
            val cx = dx.times(point.x)
            canvas.drawCircle(cx, cy, circleRadius, linePaint)
            if (point.x > 0) {
                canvas.drawLine(prevX, prevY, cx, cy, linePaint)
                if (point.x == xRange.toFloat()) break
            }
            prevX = cx
            prevY = cy
        }*/
        canvas.restore()
    }

    fun drawHorizontalLabels(canvas: Canvas, labels: List<String>) {
        canvas.save()
        canvas.translate(chartRect.left, 0f)
        val y = chartRect.bottom + fontPaint.textSize
        for (label in labels) {
            val w = fontPaint.measureText(label)
            canvas.drawText(label, -(w / 2f), y, fontPaint)
            canvas.translate(dx, 0f)
        }
        canvas.restore()
    }

    fun drawVerticalLabels(canvas: Canvas) {
        canvas.save()
        canvas.translate(chartRect.left / 4f, chartRect.top)

        val m = maxY.toInt()
        for (i in m downTo 0 step 2) {
            if (i != 0) {
                canvas.drawText("$i", 0f, 0f, fontPaint)
                canvas.translate(0f, dy * 2)
            }
        }
        canvas.restore()
    }
}