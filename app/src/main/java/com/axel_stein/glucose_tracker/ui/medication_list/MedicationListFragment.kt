package com.axel_stein.glucose_tracker.ui.medication_list

import android.os.Bundle
import android.util.SparseArray
import android.view.*
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Medication
import com.axel_stein.glucose_tracker.databinding.FragmentMedicationListBinding
import com.axel_stein.glucose_tracker.ui.log_list.TextHeaderDecor
import com.axel_stein.glucose_tracker.ui.medication_list.MedicationListFragmentDirections.Companion.actionMedicationListAdd
import com.axel_stein.glucose_tracker.ui.medication_list.MedicationListFragmentDirections.Companion.actionMedicationListEdit
import com.axel_stein.glucose_tracker.utils.setShown

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
            findNavController().navigate(actionMedicationListEdit(item.id))
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(headerDecor)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, {
            updateHeaders(it)
            adapter.submitList(it)
            binding.textEmpty.setShown(it.isEmpty())
        })
    }

    private fun updateHeaders(list: List<Medication>) {
        val headers = SparseArray<String>()
        var active: Boolean? = null
        list.forEachIndexed { index, item ->
            val itemActive = item.active
            if (active == null || active != itemActive) {
                headers[index] = getString(if (itemActive) R.string.active_medications else R.string.suspended_medications)
                active = itemActive
            }
        }
        headerDecor.setHeaders(headers)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_medication_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> findNavController().navigate(actionMedicationListAdd())
        }
        return super.onOptionsItemSelected(item)
    }
}