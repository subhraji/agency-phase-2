package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.OngoingJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel

class OngoingJobsAdapter (private val itemList: List<TestModel>,
                          private val context: Context,
                          private val isBid: Boolean):
    RecyclerView.Adapter<OngoingJobsAdapter.DashQuickCallsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingJobsAdapter.DashQuickCallsViewHolder {
        val itemBinding = OngoingJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OngoingJobsAdapter.DashQuickCallsViewHolder(itemBinding,isBid)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OngoingJobsAdapter.DashQuickCallsViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class DashQuickCallsViewHolder(private val itemBinding: OngoingJobsItemLayoutBinding, private val isBid: Boolean) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: TestModel, context: Context) {

            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
            }
        }

    }
}