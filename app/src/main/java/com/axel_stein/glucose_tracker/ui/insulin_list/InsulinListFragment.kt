package com.axel_stein.glucose_tracker.ui.insulin_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.databinding.FragmentInsulinListBinding

class InsulinListFragment : Fragment() {
    private var _binding: FragmentInsulinListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsulinListBinding.inflate(inflater, container, false)
        val adapter = InsulinListAdapter()
        adapter.submitList(
            listOf(Insulin("Humalog"), Insulin("Actrapid"), Insulin("Lantus"))
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_insulin_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    // protected val viewModel: LogListViewModel by viewModels()
    // private lateinit var adapter: LogListAdapter
    // private val headerDecor = TextHeaderDecor(R.layout.item_date)
    /*
    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        setupRecyclerView(binding.recyclerView)
        return binding.root
    }

    protected fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManagerWrapper(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(headerDecor)

        adapter = LogListAdapter()
        adapter.setOnItemClickListener { _, item ->
            findNavController().navigate(
                    when (item.itemType) {
                        0 -> EditGlucoseActivityDirections.launchEditGlucose(item.id)
                        1 -> EditA1cActivityDirections.launchEditNote(item.id)
                        else -> EditA1cActivityDirections.launchEditA1c(item.id)
                    }
            )
        }
        recyclerView.adapter = adapter
    }

    protected open fun textEmpty() = binding.textEmpty

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData().observe(viewLifecycleOwner, { list ->
            updateHeaders(list)
            adapter.submitList(list)
            textEmpty().setShown(list.isEmpty())
        })
    }

    private fun updateHeaders(list: List<LogItem>?) {
        val headers = SparseArray<String>()
        var date: LocalDate? = null
        list?.forEachIndexed { index, item ->
            val itemDate = item.dateTime.toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(requireContext(), item.dateTime)
                date = itemDate
            }
            item.timeFormatted = formatTime(requireContext(), item.dateTime)
        }
        headerDecor.setHeaders(headers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    */
}