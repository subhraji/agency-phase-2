package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ClientListItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.EditDeleteClickListener
import com.user.caregiver.gone

class ClientListAdapter (private val itemList: MutableList<Data>,
                         private val context: Context,
                         private val editDeleteClickListener: EditDeleteClickListener
):
    RecyclerView.Adapter<ClientListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientListAdapter.ViewHolder {
        val itemBinding = ClientListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClientListAdapter.ViewHolder(itemBinding, editDeleteClickListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ClientListAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: ClientListItemLayoutBinding, private val editDeleteClickListener: EditDeleteClickListener) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context) {
            itemBinding.apply {
                addBtn.gone()
                editBtn.gone()
                fullNameTv.text = data.name.toString()
                emailTv.text = data.email.toString()
                mobileTv.text = data.phone.toString()
                Glide.with(context).load(Constants.PUBLIC_URL+ data.photo)
                    .placeholder(R.color.white)
                    .into(clientImg)
                deleteBtn.setOnClickListener {
                    editDeleteClickListener.onClick(it.rootView, data.client_id)
                }
            }
        }
    }
}