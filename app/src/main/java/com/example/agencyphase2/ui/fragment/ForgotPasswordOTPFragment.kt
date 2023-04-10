package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentForgotPasswordOtpBinding
import com.example.agencyphase2.databinding.FragmentJobPostBinding

class ForgotPasswordOTPFragment : Fragment() {
    private var _binding: FragmentForgotPasswordOtpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordOtpBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
}