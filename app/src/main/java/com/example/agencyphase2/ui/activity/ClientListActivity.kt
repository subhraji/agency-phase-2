package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ClientListAdapter
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityClientListBinding
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.pojo.TestModel

class ClientListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("a"))
        list.add(TestModel("b"))
        list.add(TestModel("c"))
        list.add(TestModel("d"))

        fillClientListRecycler(list)

        binding.backArrow.setOnClickListener {
            finish()
        }

    }

    private fun fillClientListRecycler(list: List<TestModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.clientListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = ClientListAdapter(list.toMutableList(),this@ClientListActivity)
        }
    }
}