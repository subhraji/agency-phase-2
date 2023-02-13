package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.AuthOfficerItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_authorize_officer.Data
import com.example.agencyphase2.utils.EditDeleteClickListener
import com.user.caregiver.gone
import com.user.caregiver.visible

class AuthorizeOfficerAdapter (private val itemList: List<Data>,
                               private val context: Context,
                               private val editDeleteClickListener: EditDeleteClickListener):
    RecyclerView.Adapter<AuthorizeOfficerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorizeOfficerAdapter.ViewHolder {
        val itemBinding = AuthOfficerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuthorizeOfficerAdapter.ViewHolder(itemBinding, editDeleteClickListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AuthorizeOfficerAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context, position)
    }

    class ViewHolder(private val itemBinding: AuthOfficerItemLayoutBinding, private val editDeleteClickListener: EditDeleteClickListener) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context, position: Int) {

            itemBinding.apply {
                data?.name?.let {
                    fullNameTv.text = it
                }
                data?.email?.let {
                    emailTv.text = it
                }
                data?.role?.let {
                    roleTv.text = it
                }
                data?.phone?.let {
                    mobileTv.text = it
                }

                deleteBtn.setOnClickListener {
                    editDeleteClickListener.onClick(it.rootView, data?.user_id)
                }
            }
        }

    }
}