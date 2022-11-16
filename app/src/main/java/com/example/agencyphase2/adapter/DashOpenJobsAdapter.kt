package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.OpenJobItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel

class DashOpenJobsAdapter (private val itemList: List<TestModel>,
                           private val context: Context,
                           private val isBid: Boolean):
    RecyclerView.Adapter<DashOpenJobsAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashOpenJobsAdapter.DashQuickCallsViewHolder {
        val itemBinding = OpenJobItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashOpenJobsAdapter.DashQuickCallsViewHolder(itemBinding,isBid)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashOpenJobsAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: OpenJobItemLayoutBinding, private val isBid: Boolean) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {

            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
            }
        }

    }
}