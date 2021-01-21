package com.axel_stein.glucose_tracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.list.log_list.LogListViewHelper

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    private var _viewHelper: LogListViewHelper? = null
    private val viewHelper get() = _viewHelper!!

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
        viewModel.logListLiveData.observe(viewLifecycleOwner, {
            viewHelper.submitLogList(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewHelper = null
    }
}