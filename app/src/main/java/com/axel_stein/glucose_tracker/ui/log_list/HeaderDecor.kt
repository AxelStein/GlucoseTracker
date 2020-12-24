package com.axel_stein.glucose_tracker.ui.log_list

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.util.LruCache
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*


class HeaderDecor(private val adapter: HeaderAdapter) : ItemDecoration() {
    private var cache: LruCache<Int, TextView> = LruCache(10)
    private var itemOffsetFirst = 0
    private var itemOffset = 0

    interface HeaderAdapter {
        fun hasHeader(position: Int): Boolean
        fun inflateHeaderView(): TextView
        fun getHeaderTitle(position: Int): String
    }

    fun invalidate() {
        cache = LruCache(10)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        val position = parent.getChildAdapterPosition(view)
        val first = position == 0

        if (position != NO_POSITION && adapter.hasHeader(position)) {
            var headerView = cache[position]
            if (headerView == null) {
                headerView = adapter.inflateHeaderView()
                cache.put(position, headerView)
                measureHeaderView(headerView, parent)
            }
            val offset = if (first) itemOffsetFirst else itemOffset
            if (offset != 0) {
                outRect.top = offset
            } else {
                val lp = headerView.layoutParams as MarginLayoutParams
                val margin = if (first) 0 else lp.topMargin
                outRect.top = headerView.height + margin
                if (first) {
                    itemOffsetFirst = outRect.top
                } else {
                    itemOffset = outRect.top
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position != NO_POSITION && adapter.hasHeader(position)) {
                val headerView = cache[position]
                if (headerView != null) {
                    if (headerView.tag != position) {
                        headerView.tag = position
                        headerView.text = adapter.getHeaderTitle(position)
                    }
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
            view.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        val displayMetrics = parent.context.resources.displayMetrics
        val widthSpec = makeMeasureSpec(displayMetrics.widthPixels, EXACTLY)
        val heightSpec = makeMeasureSpec(displayMetrics.heightPixels, EXACTLY)
        val childWidth = getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom, view.layoutParams.height)
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}