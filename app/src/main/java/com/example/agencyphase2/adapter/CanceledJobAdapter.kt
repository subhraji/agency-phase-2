package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.CompleteJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_canceled_job.Data
import com.example.agencyphase2.ui.activity.CompleteJobDetailsActivity
import com.user.caregiver.gone

class CanceledJobAdapter (private val itemList: List<Data>,
                          private val context: Context
):
    RecyclerView.Adapter<CanceledJobAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanceledJobAdapter.ViewHolder {
        val itemBinding = CompleteJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CanceledJobAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CanceledJobAdapter.ViewHolder, position: Int) {
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
                dateHtv.text = data?.start_date.toString()+"-"+data?.end_date.toString()
                hourHtv.text = data?.start_time+" to "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()

                rootLay.setOnClickListener {
                    /*val intent = Intent(context, CompleteJobDetailsActivity::class.java)
                    intent.putExtra("id",data?.job_id)
                    context.startActivity(intent)*/
                }
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.gender+": "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+": "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen

                statusTv.text = "Canceled Job"

                statusTvLay.setBackgroundTintList(
                    ColorStateList.valueOf(context.resources.getColor(
                        R.color.error_red)))
                timeLeftTv.setBackgroundTintList(
                    ColorStateList.valueOf(context.resources.getColor(
                        R.color.error_red)))

                timeLeftTv.gone()
            }

        }

    }
}