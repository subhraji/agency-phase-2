package com.example.agencyphase2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentClosedBinding
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.ui.activity.*

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postJobCardBtn.setOnClickListener {
            val intent = Intent(requireActivity(), JobPostActivity::class.java)
            startActivity(intent)
        }

        binding.textView1.setOnClickListener {
            showCompleteDialog()
        }
    }

    private fun showCompleteDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)
        complete.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(requireActivity(), BasicInformationActivity::class.java)
            startActivity(intent)

        }
        dialog.show()
    }
}