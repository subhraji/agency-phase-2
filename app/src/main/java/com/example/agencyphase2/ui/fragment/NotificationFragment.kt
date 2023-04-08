package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.CompletedJobAdapter
import com.example.agencyphase2.adapter.NotificationListAdapter
import com.example.agencyphase2.databinding.FragmentLoginBinding
import com.example.agencyphase2.databinding.FragmentNotificationBinding
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.model.pojo.get_complete_jobs.Data

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))

        fillRecyclerView(list)
    }

    private fun fillRecyclerView(list: MutableList<TestModel>) {
        val linearlayoutManager = LinearLayoutManager(activity)
        binding.notificationRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = NotificationListAdapter(list,requireActivity())
        }
    }
}