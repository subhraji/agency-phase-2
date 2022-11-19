package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.databinding.AuthOfficerItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_authorize_officer.Data

class AuthorizeOfficerAdapter (private val itemList: List<Data>,
                               private val context: Context):
    RecyclerView.Adapter<AuthorizeOfficerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorizeOfficerAdapter.ViewHolder {
        val itemBinding = AuthOfficerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuthorizeOfficerAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AuthorizeOfficerAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: AuthOfficerItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {

            itemBinding.apply {
                //openJobAmountTv.text = "$"+data.amount_per_hour
                nameTv.text = data?.firstname.toString()+" "+data?.lastname.toString()
            }
        }

    }
}