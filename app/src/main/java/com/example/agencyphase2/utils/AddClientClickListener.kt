package com.example.agencyphase2.utils

import android.view.View
import com.example.agencyphase2.model.pojo.get_clients.Data

interface AddClientClickListener {
    fun onClick(view: View, data: Data)
}