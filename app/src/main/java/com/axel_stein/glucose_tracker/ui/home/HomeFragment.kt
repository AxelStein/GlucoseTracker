package com.axel_stein.glucose_tracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.glucose_tracker.databinding.FragmentLogListBinding
import com.axel_stein.glucose_tracker.ui.log_list.LogListViewHelper
import com.google.android.material.transition.MaterialFadeThrough

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    private var _viewHelper: LogListViewHelper? = null
    private val viewHelper get() = _viewHelper!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()
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
        postponeEnterTransition()
        viewModel.logListLiveData.observe(viewLifecycleOwner, {
            viewHelper.submitLogList(it)
            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewHelper = null
    }
}