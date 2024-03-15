package com.example.agencyphase2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ClientListItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_clients.Data
import com.example.agencyphase2.utils.AddClientClickListener
import com.example.agencyphase2.utils.Constants
import com.user.caregiver.gone

class SearchClientAdapter(
    private val addClientClickListener: AddClientClickListener
) : RecyclerView.Adapter<SearchClientAdapter.ViewHolder>() {

    inner class ViewHolder(
        val itemClientListItemLayoutBinding: ClientListItemLayoutBinding,
    ): RecyclerView.ViewHolder(itemClientListItemLayoutBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ClientListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.itemClientListItemLayoutBinding.apply {
            editBtn.gone()
            deleteBtn.gone()

            fullNameTv.text = data.name.toString()
            emailTv.text = data.email.toString()
            mobileTv.text = data.phone.toString()
            Glide.with(this.clientImg.context).load(Constants.PUBLIC_URL+ data.photo)
                .placeholder(R.color.color_grey)
                .into(clientImg)

            root.setOnClickListener {
                addClientClickListener.onAddClick(it.rootView, data)
            }
        }
    }
}