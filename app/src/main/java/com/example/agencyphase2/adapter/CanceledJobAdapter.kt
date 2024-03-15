package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.CompleteJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_canceled_job.DataX
import com.example.agencyphase2.ui.activity.IncompleteJobDetailsActivity
import com.user.caregiver.gone

class CanceledJobAdapter (private val itemList: MutableList<DataX>,
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
    fun add(jobs: List<DataX>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }
    override fun onBindViewHolder(holder: CanceledJobAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: CompleteJobsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: DataX, context: Context) {
            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                dateHtv.text = data?.start_date.toString()+"-"+data?.end_date.toString()
                hourHtv.text = data?.start_time+" to "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()

                rootLay.setOnClickListener {
                    val intent = Intent(context, IncompleteJobDetailsActivity::class.java)
                    intent.putExtra("id",data?.id)
                    context.startActivity(intent)
                }
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.patient_name+", "+i.gender+": "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+": "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen

                statusTv.text = data?.status

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