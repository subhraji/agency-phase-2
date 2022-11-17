package com.example.agencyphase2.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentClosedBinding
import com.example.agencyphase2.databinding.FragmentHomeBinding
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.*
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.AddBusinessInfoViewModel
import com.example.agencyphase2.viewmodel.GetPostJobsViewModel
import com.user.caregiver.isConnectedToInternet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetPostJobsViewModel: GetPostJobsViewModel by viewModels()

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

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.postJobCardBtn.setOnClickListener {
            val intent = Intent(requireActivity(), JobPostActivity::class.java)
            startActivity(intent)
        }

        binding.textView1.setOnClickListener {
            showCompleteDialog()
        }

        //api call
        if(requireActivity().isConnectedToInternet()){
            mGetPostJobsViewModel.getPostJobs(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }

        //observer
        getPostJobsObserve()
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

    private fun getPostJobsObserve(){
        mGetPostJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mGetPostJobsViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}