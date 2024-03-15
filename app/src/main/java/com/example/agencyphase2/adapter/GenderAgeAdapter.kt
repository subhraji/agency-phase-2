package com.example.agencyphase2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.GenderAgeItemLayoutBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.ui.activity.JobPostActivity

class GenderAgeAdapter (private val itemList: MutableList<GenderAgeItemCountModel>,
                        private val context: Context,
                        private val careType: String):
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

    fun remove(genderAgeItemCountModel: GenderAgeItemCountModel) {
        itemList.remove(genderAgeItemCountModel)
        /*JobPostActivity.genderAgeList.remove(genderAgeItemCountModel)
        JobPostActivity.genderAgeList = mutableListOf()*/
    }

    override fun onBindViewHolder(holder: GenderAgeAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context, careType)
        val deleteBtn = holder.itemView.findViewById<ImageView>(R.id.image_view)
        deleteBtn.setOnClickListener {
            remove(rowData)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder(private val itemBinding: GenderAgeItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: GenderAgeItemCountModel, context: Context, careType: String) {
            itemBinding.apply {
                nameTv.text = data?.patient_name
                ageTv.text = "Age: ${data?.age} years"
                careTypeTv.text = "Care type: ${careType}"
                genderTv.text = "Gender: ${data?.gender}"
            }
        }
    }
}