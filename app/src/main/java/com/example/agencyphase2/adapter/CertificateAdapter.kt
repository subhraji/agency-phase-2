package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.CertificateItemLayoutBinding
import com.example.agencyphase2.model.pojo.caregiver_profile.Certificate

class CertificateAdapter (private val itemList: MutableList<Certificate>,
                          private val context: Context):
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateAdapter.ViewHolder {
        val itemBinding = CertificateItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CertificateAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CertificateAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: CertificateItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Certificate, context: Context) {
            itemBinding.apply {
                certificateNameTv.text = data?.certificate_or_course
                durationTv.text = data?.start_year+" to "+data?.end_year
            }
        }
    }
}