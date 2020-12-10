package com.example.glucose_tracker.ui

interface OnItemClickListener<T> {
    fun onItemClick(pos: Int, item: T)
}