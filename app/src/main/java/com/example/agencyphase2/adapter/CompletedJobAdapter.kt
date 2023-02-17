package com.example.agencyphase2.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.CompleteJobsItemLayoutBinding
import com.example.agencyphase2.databinding.PostJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_complete_jobs.Data

class CompletedJobAdapter (private val itemList: List<Data>,
                           private val context: Context
):
    RecyclerView.Adapter<CompletedJobAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedJobAdapter.ViewHolder {
        val itemBinding = CompleteJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompletedJobAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CompletedJobAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: CompleteJobsItemLayoutBinding) :
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
                    /*val intent = Intent(context, PostJobsDetailsActivity::class.java)
                    intent.putExtra("id",data?.)
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

                statusTvLay.setBackgroundTintList(
                    ColorStateList.valueOf(context.resources.getColor(
                        R.color.color_green)))
                timeLeftTv.setBackgroundTintList(
                    ColorStateList.valueOf(context.resources.getColor(
                        R.color.color_green)))

            }
        }

    }
}