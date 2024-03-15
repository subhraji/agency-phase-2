package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.CheckListItemAdapter
import com.example.agencyphase2.adapter.CheckLocationNotificationAdapter
import com.example.agencyphase2.adapter.NotificationListAdapter
import com.example.agencyphase2.databinding.ActivityIncompleteJobDetailsBinding
import com.example.agencyphase2.databinding.ActivityJobActivitiesBinding
import com.example.agencyphase2.model.pojo.TestModel

class JobActivitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobActivitiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearBtn.setOnClickListener {
            finish()
        }

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))
        list.add(TestModel("cd"))

        fillCheckListRecyclerView(list)
        fillCheckNotiRecyclerView(list)
    }

    private fun fillCheckListRecyclerView(list: MutableList<TestModel>) {
        val linearlayoutManager = LinearLayoutManager(this)
        binding.checkListItemRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = CheckListItemAdapter(list,this@JobActivitiesActivity)
        }
    }

    private fun fillCheckNotiRecyclerView(list: MutableList<TestModel>) {
        val linearlayoutManager = LinearLayoutManager(this)
        binding.checkNotificationRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = CheckLocationNotificationAdapter(list,this@JobActivitiesActivity)
        }
    }
}