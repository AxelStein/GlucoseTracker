package com.axel_stein.glucose_tracker.ui.insulin_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentInsulinListBinding
import com.axel_stein.glucose_tracker.ui.insulin_list.InsulinListFragmentDirections.Companion.actionInsulinListEditInsulin
import com.axel_stein.glucose_tracker.utils.setShown

class InsulinListFragment : Fragment() {
    private val viewModel: InsulinListViewModel by viewModels()
    private var _binding: FragmentInsulinListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: InsulinListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsulinListBinding.inflate(inflater, container, false)
        adapter = InsulinListAdapter()
        adapter.setOnItemClickListener { _, item ->
            findNavController().navigate(actionInsulinListEditInsulin(item.id))
        }
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.textEmpty.setShown(it.isEmpty())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_insulin_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> findNavController().navigate(actionInsulinListEditInsulin())
        }
        return super.onOptionsItemSelected(item)
    }
}