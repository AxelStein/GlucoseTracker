package com.axel_stein.glucose_tracker.ui

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.forEach

class AnimationHelper {
    fun rotateFab(fab: View, rotate: Boolean) {
        fab.animate().apply {
            duration = 200
            rotation(if (rotate) 135f else 0f)
        }.start()
    }

    fun showDim(dim: View) {
        dim.visibility = VISIBLE
        dim.alpha = 0f
        dim.animate().apply {
            duration = 200
            alpha(1f)
        }.start()
    }

    fun hideDim(dim: View) {
        dim.animate().apply {
            duration = 200
            alpha(0f)
            withEndAction { dim.visibility = View.INVISIBLE }
        }.start()
    }

    fun initFabMenu(fabMenu: ViewGroup) {
        fabMenu.forEach { child ->
            child.visibility = GONE
            child.alpha = 0f
            child.translationY = child.height.toFloat()
        }
    }

    fun showFabMenu(fabMenu: ViewGroup) {
        fabMenu.forEach { child ->
            showIn(child)
        }
    }

    fun hideFabMenu(fabMenu: ViewGroup) {
        fabMenu.forEach { child ->
            showOut(child)
        }
    }

    private fun showIn(view: View) {
        view.visibility = VISIBLE
        view.alpha = 0f
        view.translationY = view.height.toFloat()

        view.animate().apply {
            duration = 200
            translationY(0f)
            alpha(1f)
        }.start()
    }

    private fun showOut(view: View) {
        view.visibility = VISIBLE
        view.alpha = 1f
        view.translationY = 0f

        view.animate().apply {
            duration = 200
            translationY(view.height.toFloat())
            withEndAction { view.visibility = View.GONE }
            alpha(0f)
        }.start()
    }
}