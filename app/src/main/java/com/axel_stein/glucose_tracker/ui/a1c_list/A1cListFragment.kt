package com.axel_stein.glucose_tracker.ui.a1c_list

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.a1c_list.A1cListFragmentDirections.Companion.actionAddA1c
import com.axel_stein.glucose_tracker.ui.log_list.LogListViewHelper
import com.google.android.material.transition.MaterialSharedAxis

class A1cListFragment : Fragment() {
    private val viewModel: A1cListViewModel by viewModels()

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    private var _viewHelper: LogListViewHelper? = null
    private val viewHelper get() = _viewHelper!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        _viewHelper = LogListViewHelper(
            binding.recyclerView,
            binding.textEmpty,
            findNavController()
        )
        viewHelper.setRecyclerViewBottomPadding(resources.getDimensionPixelSize(R.dimen.padding_small))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        viewModel.logListLiveData.observe(viewLifecycleOwner, {
            viewHelper.submitLogList(it)
            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add ->
                findNavController().navigate(actionAddA1c())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewHelper = null
    }
}