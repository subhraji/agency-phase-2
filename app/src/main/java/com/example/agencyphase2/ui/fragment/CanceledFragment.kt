package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.adapter.CanceledJobAdapter
import com.example.agencyphase2.adapter.PostJobsAdapter
import com.example.agencyphase2.databinding.FragmentCanceledBinding
import com.example.agencyphase2.model.pojo.get_canceled_job.DataX
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PaginationScrollListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCanceledJobViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CanceledFragment : Fragment() {
    private var _binding: FragmentCanceledBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetCanceledJobViewModel: GetCanceledJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page_no = 1
    lateinit var adapter: CanceledJobAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCanceledBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = requireActivity().loadingDialog()

        //observer
        getCanceledJobsObserve()

        onScrollLister()
    }

    override fun onResume() {
        super.onResume()
        adapter = CanceledJobAdapter(mutableListOf(),requireActivity())
        binding.canceledJobRecycler.adapter = adapter
        binding.canceledJobRecycler.gone()
        binding.canceledJobsShimmerView.visible()
        binding.canceledJobsShimmerView.startShimmer()
        binding.noDataLottie.gone()
        page_no = 1
        if(requireActivity().isConnectedToInternet()){
            mGetCanceledJobViewModel.getCanceledJob(accessToken, page_no)
        }else{
            Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCanceledJobsObserve(){
        mGetCanceledJobViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.canceledJobsShimmerView.stopShimmer()
                    binding.canceledJobsShimmerView.gone()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.data?.size != 0){
                            binding.canceledJobRecycler.visible()
                            isLoading = false
                            adapter.add(outcome.data?.data?.data!!)
                            binding.noDataLottie.gone()
                        }else{
                            if(page_no == 1){
                                binding.noDataLottie.visible()
                            }
                        }
                        mGetCanceledJobViewModel.navigationComplete()
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

    private fun onScrollLister(){
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.canceledJobRecycler.layoutManager = layoutManager
        binding.canceledJobRecycler.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page_no++
                mGetCanceledJobViewModel.getCanceledJob(accessToken, page_no)
            }
        })
    }

    /*private fun fillRecyclerView(list: List<DataX>) {
        val linearlayoutManager = LinearLayoutManager(activity)
        binding.canceledJobRecycler.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = CanceledJobAdapter(list,requireActivity())
        }
    }*/
}