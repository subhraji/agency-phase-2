package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.PostJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_post_jobs.Data
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
        private lateinit var gen:String
        fun bind(data: Data, context: Context) {

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
                    intent.putExtra("title",data?.title)
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

                statusTv.text = data?.status.toString()

                if(data?.status == "Bidding Started"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))
                }else if(data?.status == "Quick Call"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                }else if(data?.status == "Open Job"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                }
            }
        }

    }
}