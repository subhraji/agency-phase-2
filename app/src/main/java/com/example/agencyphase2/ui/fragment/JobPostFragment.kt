package com.example.agencyphase2.ui.fragment

import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.FragmentJobPostBinding
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.PostJobPreviewActivity
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.PostJobViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.user.caregiver.gone
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import java.util.*

class JobPostFragment : Fragment() {
    private var _binding: FragmentJobPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

    }

}