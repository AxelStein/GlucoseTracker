package com.axel_stein.glucose_tracker.ui.log_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.ui.log_list.WeightListFragmentDirections.Companion.actionAddWeight

class WeightListFragment : LogListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerViewBottomPadding(resources.getDimensionPixelSize(R.dimen.padding_small))
        // fixme
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_weight_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add ->
                findNavController().navigate(actionAddWeight())
        }
        return super.onOptionsItemSelected(item)
    }
}