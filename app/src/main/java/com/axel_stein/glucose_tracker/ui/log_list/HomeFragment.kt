package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.view.View

class HomeFragment : LogListFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadRecentItems()
    }
}