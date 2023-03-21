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
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentOngoingBinding
import com.example.agencyphase2.databinding.FragmentProfileBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.ChooseLoginRegActivity
import com.example.agencyphase2.ui.activity.JobPostActivity
import com.example.agencyphase2.ui.activity.RegistrationActivity
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.ChangePasswordViewModel
import com.example.agencyphase2.viewmodel.GetProfileViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessToken: String
    private val mGetProfileViewModel: GetProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.visibility = View.INVISIBLE

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observer
        getProfileObserver()

        binding.mainLay.gone()
        binding.profileShimmerView.visible()
        binding.profileShimmerView.startShimmer()
        if(requireActivity().isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(requireActivity(),"No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProfileObserver(){
        mGetProfileViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.mainLay.visible()
                        binding.profileShimmerView.gone()
                        binding.profileShimmerView.stopShimmer()

                        Glide.with(this).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                            .placeholder(R.color.color_grey)
                            .error(R.color.error_red)
                            .into(binding.profileImg)
                        outcome.data!!.data.company_name?.let {
                            binding.nameTv.text = it
                        }
                        outcome.data!!.data.email?.let {
                            binding.emailTv.text = it
                        }
                        outcome.data!!.data.phone?.let {
                            binding.phoneTv.text = it
                        }
                        if(outcome.data!!.data.years_in_business != null){
                            binding.yearsOfBusinessTv.text = outcome.data!!.data.years_in_business.toString()
                        }else{
                            binding.yearsOfBusinessTv.text = "no data"
                        }
                        if(outcome.data!!.data.number_of_employee != null){
                            binding.numberOfEmployeeTv.text = outcome.data!!.data.number_of_employee.toString()
                        }else{
                            binding.numberOfEmployeeTv.text = "no data"
                        }
                        if(outcome.data!!.data.country_of_business != null){
                            binding.countryTv.text = outcome.data!!.data.country_of_business.toString()
                        }else{
                            binding.countryTv.text = "no data"
                        }

                        if(outcome.data!!.data.organization_type != null){
                            binding.orgTypeTv.text = outcome.data!!.data.organization_type.toString()
                        }else{
                            binding.orgTypeTv.text = "no data"
                        }
                        if(outcome.data!!.data.legal_structure != null){
                            binding.lsTv.text = outcome.data!!.data.legal_structure.toString()
                        }else{
                            binding.lsTv.text = "no data"
                        }
                        if(outcome.data!!.data.annual_business_revenue != null){
                            binding.revenueTv.text = outcome.data!!.data.annual_business_revenue.toString()
                        }else{
                            binding.revenueTv.text = "no data"
                        }
                        if(outcome.data!!.data.tax_id_or_ein_id != null){
                            binding.taxIdTv.text = outcome.data!!.data.tax_id_or_ein_id.toString()
                        }else{
                            binding.taxIdTv.text = "no data"
                        }

                        //profile status
                        if(outcome.data?.data?.profile_status?.is_business_info_complete == 0){
                            showCompleteDialog("You have not added your business information",1)
                        }else if(outcome.data?.data?.profile_status?.is_authorize_info_added == 0){
                            showCompleteDialog("You have not added any authorize officer",3)
                        }

                        mGetProfileViewModel.navigationComplete()
                    }else{
                        if(outcome.data?.http_status_code == 401){
                            PrefManager.clearPref()
                            startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                            requireActivity().finish()
                        }else{
                            Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        }
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