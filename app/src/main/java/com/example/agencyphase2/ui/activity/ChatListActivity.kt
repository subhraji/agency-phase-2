package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ChatListAdapter
import com.example.agencyphase2.adapter.NotificationListAdapter
import com.example.agencyphase2.databinding.ActivityChatBinding
import com.example.agencyphase2.databinding.ActivityChatListBinding
import com.example.agencyphase2.model.pojo.TestModel

class ChatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val list: MutableList<TestModel> = mutableListOf()
        list.add(TestModel("Ankur Warikoo"))
        list.add(TestModel("Ashnir Grover"))
        list.add(TestModel("John Cartle"))

        fillRecyclerView(list)
    }

    private fun fillRecyclerView(list: MutableList<TestModel>) {
        val linearlayoutManager = LinearLayoutManager(this)
        binding.chatListRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChatListAdapter(list,this@ChatListActivity)
        }
    }
}