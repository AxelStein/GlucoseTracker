package com.axel_stein.glucose_tracker.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

// Bug fix for IndexOutOfBoundsException is disabling predictive animations
// https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
class LinearLayoutManagerWrapper : LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    override fun supportsPredictiveItemAnimations(): Boolean = false
}