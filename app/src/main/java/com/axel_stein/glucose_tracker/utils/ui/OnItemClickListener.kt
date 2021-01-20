package com.axel_stein.glucose_tracker.utils.ui

interface OnItemClickListener<T> {
    fun onItemClick(pos: Int, item: T)
}