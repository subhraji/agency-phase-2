package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.UpcomingListItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_upcomming_jobs.Data
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity

class UpcomingJobsAdapter (private val itemList: List<Data>,
                           private val context: Context
):
    RecyclerView.Adapter<UpcomingJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingJobsAdapter.ViewHolder {
        val itemBinding = UpcomingListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpcomingJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: UpcomingJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: UpcomingListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var gen: String = ""
        fun bind(data: Data, context: Context) {

            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                dateHtv.text = data?.date.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()
                rootLay.setOnClickListener {
                    /*val intent = Intent(context, PostJobsDetailsActivity::class.java)
                    intent.putExtra("id",data?.id)
                    intent.putExtra("title",data?.title)
                    context.startActivity(intent)*/
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

                statusTv.text = data.status.toString()

            }
        }

    }
}