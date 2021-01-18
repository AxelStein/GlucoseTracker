package com.axel_stein.glucose_tracker.utils.ui

import android.content.Context
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors

@Suppress("unused", "MemberVisibilityCanBePrivate")
class IconTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var showIconStart = true
    private var showIconEnd = true
    private var iconStartColor = 0
    private var iconEndColor = 0
    private var iconStart: Drawable? = null
    private var iconEnd: Drawable? = null

    fun showIconStart(show: Boolean) {
        showIconStart = show
        update()
    }

    fun showIconEnd(show: Boolean) {
        showIconEnd = show
        update()
    }

    fun setIconStart(icon: Drawable?) {
        iconStart = icon
        update()
    }

    fun setIconEnd(icon: Drawable?) {
        iconEnd = icon
        update()
    }

    fun setIconStartRes(iconRes: Int) {
        iconStart = ContextCompat.getDrawable(context, iconRes)
        update()
    }

    fun setIconEndRes(iconRes: Int) {
        iconEnd = ContextCompat.getDrawable(context, iconRes)
        update()
    }

    fun setIconStartColor(color: Int) {
        iconStartColor = color
        update()
    }

    fun setIconEndColor(color: Int) {
        iconEndColor = color
        update()
    }

    fun setIconStartColorAttr(colorAttr: Int) {
        iconStartColor = MaterialColors.getColor(this, colorAttr)
        update()
    }

    fun setIconEndColorAttr(colorAttr: Int) {
        iconEndColor = MaterialColors.getColor(this, colorAttr)
        update()
    }

    fun setTextColorAttr(colorAttr: Int) {
        setTextColor(MaterialColors.getColor(this, colorAttr))
    }

    fun setColorAttr(colorAttr: Int) {
        setIconStartColorAttr(colorAttr)
        setIconEndColorAttr(colorAttr)
        setTextColorAttr(colorAttr)
    }

    private fun update() {
        tintIcon(iconStart, iconStartColor)
        tintIcon(iconEnd, iconEndColor)
        setCompoundDrawablesWithIntrinsicBounds(
            if (showIconStart) iconStart else null,
            null,
            iconEnd,
            if (showIconEnd) iconEnd else null
        )
    }

    private fun tintIcon(icon: Drawable?, color: Int) {
        icon?.mutate()?.colorFilter = PorterDuffColorFilter(color, SRC_ATOP)
    }
}