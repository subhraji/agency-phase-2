package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.CheckListNotificationItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel

class CheckLocationNotificationAdapter (private val itemList: MutableList<TestModel>,
                                        private val context: Context
): RecyclerView.Adapter<CheckLocationNotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckLocationNotificationAdapter.ViewHolder {
        val itemBinding = CheckListNotificationItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckLocationNotificationAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CheckLocationNotificationAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: CheckListNotificationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {
            itemBinding.apply {
            }
        }
    }

}