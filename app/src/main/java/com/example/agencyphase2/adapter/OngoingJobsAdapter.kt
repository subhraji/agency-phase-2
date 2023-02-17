package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.OngoingJobsItemLayoutBinding
import com.example.agencyphase2.databinding.OngoingListItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.model.pojo.get_ongoing_job.Data

class OngoingJobsAdapter (private val itemList: List<Data>,
                          private val context: Context):
    RecyclerView.Adapter<OngoingJobsAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingJobsAdapter.DashQuickCallsViewHolder {
        val itemBinding = OngoingListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OngoingJobsAdapter.DashQuickCallsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OngoingJobsAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: OngoingListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {

            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
            }
        }

    }
}