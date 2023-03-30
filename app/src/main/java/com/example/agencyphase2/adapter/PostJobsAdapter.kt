package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.PostJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_post_jobs.DataX
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import java.util.*

class PostJobsAdapter (private val itemList: MutableList<DataX>,
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

    fun add(jobs: List<DataX>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    override fun onBindViewHolder(holder: PostJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: PostJobsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: DataX, context: Context) {

            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                dateHtv.text = data?.date.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()
                rootLay.setOnClickListener {
                    val intent = Intent(context, PostJobsDetailsActivity::class.java)
                    intent.putExtra("id",data?.id)
                    context.startActivity(intent)
                }
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.gender+": "+i.age
                    }else{
                        gen = gen+", "+i.gender+": "+i.age
                    }
                }
                ageTv.text = gen

                //statusTv.text = data.status.toString()

                if(data?.status == "Bidding Started"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))
                }else if(data?.status == "Quick Call"){
                    statusTv.text = "Quick Call"
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                }else if(data?.status == "Open Job"){
                    statusTv.text = "Open \u00A0 Job "
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                }else if(data?.status == "Upcoming"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                }
            }
        }

    }
}