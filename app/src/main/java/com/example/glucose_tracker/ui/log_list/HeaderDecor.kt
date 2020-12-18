package com.example.glucose_tracker.ui.log_list

import android.graphics.Canvas
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class HeaderDecor(private val adapter: HeaderAdapter) : RecyclerView.ItemDecoration() {
    private var headers: SparseArray<View> = SparseArray()

    interface HeaderAdapter {
        fun hasHeader(position: Int): Boolean
        fun getHeaderView(position: Int): View
    }

    fun invalidate() {
        headers.clear()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if (position != RecyclerView.NO_POSITION && adapter.hasHeader(position)) {
            var headerView = headers[position]
            if (headerView == null) {
                headerView = adapter.getHeaderView(position)
                headers.put(position, headerView)
                measureHeaderView(headerView, parent)
            }
            val lp = headerView.layoutParams as ViewGroup.MarginLayoutParams
            val margin = if (position == 0) 0 else lp.topMargin
            outRect.top = headerView.height + margin
        } else {
            headers.remove(position)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position != RecyclerView.NO_POSITION && adapter.hasHeader(position)) {
                val headerView = headers[position]
                if (headerView != null) {
                    canvas.save()
                    canvas.translate(0f, child.y - headerView.height)
                    headerView.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun measureHeaderView(view: View, parent: ViewGroup) {
        if (view.layoutParams == null) {
            view.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val displayMetrics = parent.context.resources.displayMetrics
        val widthSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom, view.layoutParams.height)
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}