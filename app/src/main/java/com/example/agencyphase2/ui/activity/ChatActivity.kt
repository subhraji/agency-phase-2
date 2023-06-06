package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ClientListAdapter
import com.example.agencyphase2.adapter.MessageListAdapter
import com.example.agencyphase2.databinding.ActivityCareGiverProfileBinding
import com.example.agencyphase2.databinding.ActivityChatBinding
import com.example.agencyphase2.model.pojo.chat.ChatModel
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.utils.Constants

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatFrgBackArrow.setOnClickListener {
            finish()
        }

        val list: MutableList<ChatModel> = mutableListOf()
        list.add(ChatModel("hello how are you?", true))
        list.add(ChatModel("Hey long time no see, i am fine, what about you?", false))
        list.add(ChatModel("Are you ok?", true))
        fillChatRecycler(list)

        Glide.with(this).load("https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60")
            .placeholder(R.color.color_grey)
            .into(binding.userImg)
    }

    private fun fillChatRecycler(list: MutableList<ChatModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.chatRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = MessageListAdapter(list,this@ChatActivity)
        }
    }
}