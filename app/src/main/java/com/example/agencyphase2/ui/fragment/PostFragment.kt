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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.PostJobsAdapter
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.model.pojo.get_post_jobs.DataX
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.*
import com.example.agencyphase2.utils.PaginationScrollListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetPostJobsViewModel
import com.example.agencyphase2.viewmodel.GetProfileCompletionStatusViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetPostJobsViewModel: GetPostJobsViewModel by viewModels()
    private val mGetProfileCompletionStatusViewModel: GetProfileCompletionStatusViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page_no = 1
    lateinit var adapter: PostJobsAdapter

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
            if(requireActivity().isConnectedToInternet()){
                mGetProfileCompletionStatusViewModel.getProfileCompletionStatus(accessToken)
                loader = requireActivity().loadingDialog()
                loader.show()
            }else{
                Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }

        //shimmer
        binding.postJobsShimmerView.gone()

        onScrollLister()
    }

    override fun onResume() {

        if(requireActivity().isConnectedToInternet()){
            adapter = PostJobsAdapter(mutableListOf(),requireActivity())
            binding.postJobsRecycler.adapter = adapter
            binding.postJobsShimmerView.visible()
            binding.postJobsShimmerView.startShimmer()
            binding.postJobCardBtn.gone()
            binding.textView1.gone()
            binding.postJobsRecycler.gone()
            page_no = 1
            mGetPostJobsViewModel.getPostJobs(accessToken,0, page_no)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.", Toast.LENGTH_SHORT).show()
        }

        //observer
        getPostJobsObserve()
        getProfileCompletionStatusObserver()

        super.onResume()
    }

    private fun onScrollLister(){
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.postJobsRecycler.layoutManager = layoutManager
        binding.postJobsRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page_no++
                mGetPostJobsViewModel.getPostJobs(accessToken,0, page_no)
            }
        })
    }

    private fun getPostJobsObserve(){
        mGetPostJobsViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.postJobsShimmerView.stopShimmer()
                    binding.postJobsShimmerView.gone()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.data?.size != 0){
                            binding.postJobsRecycler.visible()
                            binding.textView1.gone()
                            binding.postJobCardBtn.gone()
                            //fillRecyclerView(outcome.data?.data?.data!!)
                            isLoading = false
                            adapter.add(outcome.data?.data?.data!!)
                        }else{
                            if(page_no == 1){
                                binding.postJobsRecycler.gone()
                                binding.textView1.visible()
                                binding.postJobCardBtn.visible()
                            }
                        }
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

    private fun getProfileCompletionStatusObserver(){
        mGetProfileCompletionStatusViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data?.is_business_info_complete == 0){
                            showCompleteDialog("You have not added your business information",1)
                        }else if(outcome.data?.data?.is_authorize_info_added == 0){
                            showCompleteDialog("You have not added any authorize officer",3)
                        }else if(outcome.data?.data?.is_profile_approved == 0){
                            showCompleteDialog("Your profile is not approved yet.",4)

                            /*else if(outcome.data?.data?.is_other_info_added == 0){
                                showCompleteDialog("You have not added other optional information",2)
                            }*/
                        }else{
                            val intent = Intent(requireActivity(), JobPostActivity::class.java)
                            startActivity(intent)
                        }
                        mGetProfileCompletionStatusViewModel.navigationComplete()
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

    private fun showCompleteDialog(content: String, step: Int) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.profile_completion_dialog_layout)
        val complete = dialog.findViewById<TextView>(R.id.complete_btn)
        val content_tv = dialog.findViewById<TextView>(R.id.content_tv)

        content_tv.text = content

        if(step == 4){
            complete.text = "Ok"
        }
        complete.setOnClickListener {
            if(step == 4){
                dialog.dismiss()
                val intent = Intent(requireActivity(), JobPostActivity::class.java)
                intent.putExtra("step",step)
                startActivity(intent)
            }else{
                dialog.dismiss()
                val intent = Intent(requireActivity(), RegistrationActivity::class.java)
                intent.putExtra("step",step)
                startActivity(intent)
            }
        }

        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

}