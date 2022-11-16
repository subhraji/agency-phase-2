package com.example.agencyphase2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.GenderAgeItemLayoutBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel

class GenderAgeAdapter (private val itemList: List<GenderAgeItemCountModel>,
                        private val context: Context,
                        private val careType: String,
):
    RecyclerView.Adapter<GenderAgeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderAgeAdapter.ViewHolder {
        val itemBinding = GenderAgeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenderAgeAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: GenderAgeAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context, careType)
    }

    class ViewHolder(private val itemBinding: GenderAgeItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: GenderAgeItemCountModel, context: Context, careType: String) {
            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
                genderAgeTv2.text = data?.gender+": "+data?.age
                careTypeTv.text = careType

            }
        }

    }
}