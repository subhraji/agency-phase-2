package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.CertificateAdapter
import com.example.agencyphase2.databinding.FragmentCaregiverProfileBinding
import com.example.agencyphase2.model.pojo.caregiver_profile.Certificate
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCaregiverProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaregiverProfileFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCaregiverProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private val mGetCaregiverProfileViewModel: GetCaregiverProfileViewModel by viewModels()
    private var job_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job_id = arguments?.getString("id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaregiverProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val extras = intent.extras
        if (extras != null) {
            job_id = intent?.getStringExtra("job_id")!!
        }*/

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = requireActivity().loadingDialog()

        /*binding.backBtn.setOnClickListener {
            finish()
        }*/

        //observe
        getCaregiverProfileObserve()

        if(requireActivity().isConnectedToInternet()){
            mGetCaregiverProfileViewModel.getCaregiverProfile(accessToken, job_id)
            loader.show()
        }else{
            Toast.makeText(requireActivity(),"Oops!! no internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCaregiverProfileObserve(){
        mGetCaregiverProfileViewModel.response.observe(requireActivity(), Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        binding.nameTv.text = outcome.data?.data?.name.toString()

                        outcome.data?.data?.gender?.let {
                            binding.genderTv.text = it
                        }
                        outcome.data?.data?.experience?.let {
                            binding.phoneTv.text = it.toString()+" Yrs"
                        }
                        outcome.data?.data?.care_completed?.let {
                            binding.careCompletedTv.text = it.toString()
                        }
                        Glide.with(this).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                            .placeholder(R.color.color_grey)
                            .into(binding.userImage)
                        mGetCaregiverProfileViewModel.navigationComplete()

                        if(outcome.data?.data?.certificate != null && outcome.data?.data?.certificate!!.isNotEmpty()){
                            binding.certificateHtv.visible()
                            binding.certificateRecycler.visible()
                            fillCertificateRecycler(outcome.data?.data?.certificate!!.toMutableList())
                        }else{
                            binding.certificateHtv.gone()
                            binding.certificateRecycler.gone()
                        }
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

    private fun fillCertificateRecycler(list: MutableList<Certificate>) {
        val gridLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.certificateRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = CertificateAdapter(list,requireActivity())
        }
    }

    companion object {
        fun newInstance(): CaregiverProfileFragment {
            return CaregiverProfileFragment()
        }
    }

}