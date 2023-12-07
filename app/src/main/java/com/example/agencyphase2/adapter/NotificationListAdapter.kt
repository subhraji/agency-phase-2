package com.example.agencyphase2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.NotificationItemLayoutBinding
import com.example.agencyphase2.model.pojo.TestModel
import com.example.agencyphase2.model.pojo.get_notifications.Data
import com.example.agencyphase2.utils.DeleteNotificationClickListener
import com.example.agencyphase2.viewmodel.GetNotificationsViewModel
import com.example.agencyphase2.viewmodel.MarkReadNotificationViewModel

class NotificationListAdapter (private val itemList: MutableList<Data>,
                               private val context: Context,
                               private val deleteDocClickListener: DeleteNotificationClickListener):
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListAdapter.ViewHolder {
        val itemBinding = NotificationItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationListAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun add(jobs: List<Data>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    fun remove(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemList.size)
    }

    override fun onBindViewHolder(holder: NotificationListAdapter.ViewHolder, position: Int) {
        val rowData = itemList[position]
        holder.bind(rowData, context, deleteDocClickListener, position)
    }

    class ViewHolder(private val itemBinding: NotificationItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Data, context: Context, deleteDocClickListener: DeleteNotificationClickListener, position: Int) {
            itemBinding.apply {
                titleTv.text = data.type
                contentTv.text = data.content

                markReadTv.setOnClickListener {
                    deleteDocClickListener.deleteClick(data?.notification_id,position)
                }
            }
        }
    }
}