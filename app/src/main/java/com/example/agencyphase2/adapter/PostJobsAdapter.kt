package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.PostJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_post_jobs.Data

class PostJobsAdapter (private val itemList: List<Data>,
                       private val context: Context):
    RecyclerView.Adapter<PostJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostJobsAdapter.ViewHolder {
        val itemBinding = PostJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PostJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: PostJobsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {

            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
            }
        }

    }
}