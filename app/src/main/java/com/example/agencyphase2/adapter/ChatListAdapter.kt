package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ChatListItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.ui.activity.ChatActivity
import com.example.agencyphase2.ui.activity.ChatListActivity
import com.example.agencyphase2.utils.Constants

class ChatListAdapter (private val itemList: MutableList<TestModel>,
                       private val context: Context
):
    RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        val itemBinding = ChatListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: ChatListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {

                userName.text = data.name

                root.setOnClickListener {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("caregiver_id","1")
                    intent.putExtra("name",data.name)
                    intent.putExtra("photo","https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=870&q=80")
                    context.startActivity(intent)
                }

                Glide.with(context).load("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=870&q=80")
                    .placeholder(R.color.color_grey)
                    .into(userImage)
            }
        }
    }
}