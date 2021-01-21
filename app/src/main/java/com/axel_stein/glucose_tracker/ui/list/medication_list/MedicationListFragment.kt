package com.axel_stein.glucose_tracker.ui.list.medication_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.FragmentMedicationListBinding
import com.axel_stein.glucose_tracker.ui.list.log_list.TextHeaderDecor
import com.axel_stein.glucose_tracker.ui.list.medication_list.MedicationListFragmentDirections.Companion.actionAddMedication
import com.axel_stein.glucose_tracker.ui.list.medication_list.MedicationListFragmentDirections.Companion.actionEditMedication
import com.axel_stein.glucose_tracker.utils.ui.LinearLayoutManagerWrapper
import com.axel_stein.glucose_tracker.utils.ui.setShown

class MedicationListFragment : Fragment() {
    private val viewModel: MedicationListViewModel by viewModels()
    private var _binding: FragmentMedicationListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MedicationListAdapter
    private val headerDecor = TextHeaderDecor(R.layout.item_header)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMedicationListBinding.inflate(inflater, container, false)
        adapter = MedicationListAdapter()
        adapter.setOnItemClickListener { _, item ->
            findNavController().navigate(actionEditMedication(item.id))
        }

        binding.recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext(), VERTICAL, false)
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
            binding.recyclerView.post {
                binding.recyclerView.invalidateItemDecorations()
            }
            binding.textEmpty.setShown(it.list.isEmpty())
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> findNavController().navigate(actionAddMedication())
        }
        return super.onOptionsItemSelected(item)
    }
}