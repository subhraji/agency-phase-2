package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.DashOpenJobsAdapter
import com.example.agencyphase2.databinding.FragmentClosedBinding
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.databinding.FragmentOngoingBinding
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.model.TestModel

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

        val quickCallList = ArrayList<TestModel>()
        quickCallList.add(TestModel("a"))
        quickCallList.add(TestModel("b"))
        quickCallList.add(TestModel("c"))
        fillQuickCallsRecycler(quickCallList)
    }

    private fun fillQuickCallsRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.postJobsRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = DashOpenJobsAdapter(list,requireActivity(),true)
        }
    }
}