package com.axel_stein.glucose_tracker.ui.weight_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.log_list.LogListViewHelper
import com.axel_stein.glucose_tracker.ui.weight_list.WeightListFragmentDirections.Companion.actionAddWeight

class WeightListFragment : Fragment() {
    private val viewModel: WeightListViewModel by viewModels()

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    private var _viewHelper: LogListViewHelper? = null
    private val viewHelper get() = _viewHelper!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        _viewHelper = LogListViewHelper(
            binding.recyclerView,
            binding.textEmpty,
            findNavController()
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewHelper.setRecyclerViewBottomPadding(resources.getDimensionPixelSize(R.dimen.padding_small))
        viewModel.logListLiveData.observe(viewLifecycleOwner, {
            viewHelper.submitLogList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add ->
                findNavController().navigate(actionAddWeight())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewHelper = null
    }
}