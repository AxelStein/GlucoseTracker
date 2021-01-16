package com.axel_stein.glucose_tracker.ui.insulin_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentInsulinListBinding
import com.axel_stein.glucose_tracker.ui.insulin_list.InsulinListFragmentDirections.Companion.actionAddInsulin
import com.axel_stein.glucose_tracker.ui.insulin_list.InsulinListFragmentDirections.Companion.actionEditInsulin
import com.axel_stein.glucose_tracker.ui.log_list.TextHeaderDecor
import com.axel_stein.glucose_tracker.utils.ui.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.ui.setShown

class InsulinListFragment : Fragment() {
    private val viewModel: InsulinListViewModel by viewModels()
    private var _binding: FragmentInsulinListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: InsulinListAdapter
    private val headerDecor = TextHeaderDecor(R.layout.item_header)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsulinListBinding.inflate(inflater, container, false)
        adapter = InsulinListAdapter()
        adapter.setOnItemClickListener { _, item ->
            findNavController().navigate(actionEditInsulin(item.id))
        }

        binding.recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(headerDecor)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, {
            adapter.submitList(it.list)
            headerDecor.setHeaders(it.headers)
            binding.textEmpty.setShown(it.list.isEmpty())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> findNavController().navigate(actionAddInsulin())
        }
        return super.onOptionsItemSelected(item)
    }
}