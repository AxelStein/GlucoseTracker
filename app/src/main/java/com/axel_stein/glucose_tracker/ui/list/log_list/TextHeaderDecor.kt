package com.axel_stein.glucose_tracker.ui.list.log_list

import android.graphics.Canvas
import android.graphics.Rect
import android.util.LruCache
import android.util.SparseArray
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.axel_stein.glucose_tracker.utils.ui.inflate


class TextHeaderDecor(private val headerResourceId: Int) : ItemDecoration() {
    private var headers = SparseArray<String>()
    private var cache: LruCache<Int, TextView> = LruCache(10)
    private var itemOffsetFirst = 0
    private var itemOffset = 0

    private var invalidate = false
    private var measured = false
    private var itemWidthSpec = 0
    private var itemHeightSpec = 0
    private var itemMeasuredWidth = 0
    private var itemMeasuredHeight = 0

    fun setHeaders(headers: SparseArray<String>) {
        this.headers = headers
        cache = LruCache(10)
        invalidate = true
    }

    private fun hasHeader(position: Int): Boolean = headers.indexOfKey(position) >= 0

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        val position = parent.getChildAdapterPosition(view)
        val first = position == 0

        if (position != NO_POSITION && hasHeader(position)) {
            var headerView = cache[position]
            if (headerView == null) {
                headerView = inflateHeaderView(parent, position)
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
        parent.forEach { child ->
            val position = parent.getChildAdapterPosition(child)
            if (position != NO_POSITION && hasHeader(position)) {
                var headerView = cache[position]
                if (headerView == null) {
                    headerView = inflateHeaderView(parent, position)
                }
                if (headerView.tag != position) {
                    headerView.tag = position
                    headerView.text = headers[position]
                }
                canvas.save()
                canvas.translate(0f, child.y - headerView.height)
                headerView.draw(canvas)
                canvas.restore()
            }
        }
    }

    private fun inflateHeaderView(parent: RecyclerView, position: Int): TextView {
        val view = parent.inflate(headerResourceId) as TextView
        cache.put(position, view)
        measureHeaderView(view, parent)
        return view
    }

    private fun measureHeaderView(view: View, parent: ViewGroup) {
        if (!measured) {
            if (view.layoutParams == null) {
                view.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }

            val displayMetrics = parent.context.resources.displayMetrics
            itemWidthSpec = getChildMeasureSpec(
                makeMeasureSpec(displayMetrics.widthPixels, EXACTLY),
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width
            )
            itemHeightSpec = getChildMeasureSpec(
                makeMeasureSpec(displayMetrics.heightPixels, EXACTLY),
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height
            )

            view.measure(itemWidthSpec, itemHeightSpec)
            itemMeasuredWidth = view.measuredWidth
            itemMeasuredHeight = view.measuredHeight

            measured = true
        } else {
            view.measure(itemWidthSpec, itemHeightSpec)
        }
        view.layout(0, 0, itemMeasuredWidth, itemMeasuredHeight)
    }
}