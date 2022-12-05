package com.example.agencyphase2.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ChipsItemLayoutBinding
import org.w3c.dom.Text

class ChipsAdapter(private val itemList: MutableList<String>,
                    private val context: Context):
    RecyclerView.Adapter<ChipsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipsAdapter.ViewHolder {
        val itemBinding = ChipsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChipsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChipsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context, itemList, position)

        holder.itemView.apply {
            val remove_btn: TextView = findViewById(R.id.remove_btn)
            remove_btn.setOnClickListener {
                removeChips(rowData)
                notifyDataSetChanged()
            }
        }
    }

    fun removeChips(chips: String) {
        itemList.remove(chips)
    }

    class ViewHolder(private val itemBinding: ChipsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: String, context: Context, itemList: MutableList<String>, position: Int) {
            itemBinding.apply {
                medicalHistoryTv.text = data
                /*removeBtn.setOnClickListener {
                    val adapter: ChipsAdapter = ChipsAdapter(itemList,context)
                    adapter.removeChips(data)
                    //adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()
                }*/
            }
        }
    }
}