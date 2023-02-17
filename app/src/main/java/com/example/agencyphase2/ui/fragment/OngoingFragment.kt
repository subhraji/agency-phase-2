package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.OngoingJobsAdapter
import com.example.agencyphase2.databinding.FragmentOngoingBinding
import com.example.agencyphase2.model.pojo.TestModel

class OngoingFragment : Fragment() {
    private var _binding: FragmentOngoingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOngoingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /*private fun fillQuickCallsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.postJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = OngoingJobsAdapter(list,requireActivity(),true)
        }
    }*/
}