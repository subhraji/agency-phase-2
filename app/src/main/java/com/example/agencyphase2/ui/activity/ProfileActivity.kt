package com.example.agencyphase2.ui.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityCareGiverProfileBinding
import com.example.agencyphase2.databinding.ActivityProfileBinding
import com.example.agencyphase2.model.pojo.get_profile.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetProfileViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var accessToken: String
    private val mGetProfileViewModel: GetProfileViewModel by viewModels()
    private var companyName: String? = null
    private var companyEmail: String? = null
    private var mobileNumber: String? = null
    private var taxId: String? = null
    private var noEmployee: String? = null
    private var yearsBusiness: String? = null
    private var legalStructure: String? = null
    private var annualRevenue: String? = null
    private var orgType: String? = null
    private var country: String? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        //observer
        getProfileObserver()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("mobileNumber",mobileNumber)
            intent.putExtra("noEmployee",noEmployee)
            intent.putExtra("yearsBusiness",yearsBusiness)
            intent.putExtra("legalStructure",legalStructure)
            intent.putExtra("annualRevenue",annualRevenue)
            intent.putExtra("orgType",orgType)
            intent.putExtra("country",country)
            intent.putExtra("image",image)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainLay.gone()
        binding.profileShimmerView.visible()
        binding.profileShimmerView.startShimmer()
        if(isConnectedToInternet()){
            mGetProfileViewModel.getProfile(accessToken)
        }else{
            Toast.makeText(this,"No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProfileObserver(){
        mGetProfileViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        binding.mainLay.visible()
                        binding.profileShimmerView.gone()
                        binding.profileShimmerView.stopShimmer()

                        outcome.data!!.data?.let {

                            outcome.data!!.data.photo?.let {
                                image = it
                            }
                            Glide.with(this).load(Constants.PUBLIC_URL+ outcome.data!!.data.photo)
                                .placeholder(R.color.color_grey)
                                .into(binding.profileImg)
                            outcome.data!!.data.company_name?.let {
                                binding.nameTv.text = it
                                companyName = it
                            }
                            outcome.data!!.data.email?.let {
                                binding.emailTv.text = it
                                companyEmail = it
                            }
                            outcome.data!!.data.phone?.let {
                                binding.phoneTv.text = it
                                mobileNumber = it
                            }
                            if(outcome.data!!.data.years_in_business != null){
                                binding.yearsOfBusinessTv.text = outcome.data!!.data.years_in_business.toString()
                                yearsBusiness = outcome.data!!.data.years_in_business.toString()
                            }else{
                                binding.yearsOfBusinessTv.text = "no data"
                            }
                            if(outcome.data!!.data.number_of_employee != null){
                                binding.numberOfEmployeeTv.text = outcome.data!!.data.number_of_employee.toString()
                                noEmployee = outcome.data!!.data.number_of_employee.toString()
                            }else{
                                binding.numberOfEmployeeTv.text = "no data"
                            }
                            if(outcome.data!!.data.country_of_business != null){
                                binding.countryTv.text = outcome.data!!.data.country_of_business.toString()
                                country = outcome.data!!.data.country_of_business.toString()
                            }else{
                                binding.countryTv.text = "no data"
                            }

                            if(outcome.data!!.data.organization_type != null){
                                binding.orgTypeTv.text = outcome.data!!.data.organization_type.toString()
                                orgType = outcome.data!!.data.organization_type.toString()
                            }else{
                                binding.orgTypeTv.text = "no data"
                            }
                            if(outcome.data!!.data.legal_structure != null){
                                binding.lsTv.text = outcome.data!!.data.legal_structure.toString()
                                legalStructure = outcome.data!!.data.legal_structure.toString()
                            }else{
                                binding.lsTv.text = "no data"
                            }
                            if(outcome.data!!.data.annual_business_revenue != null){
                                binding.revenueTv.text = "$ "+outcome.data!!.data.annual_business_revenue.toString()
                                annualRevenue = outcome.data!!.data.annual_business_revenue.toString()
                            }else{
                                binding.revenueTv.text = "no data"
                            }
                            if(outcome.data!!.data.tax_id_or_ein_id != null){
                                binding.taxIdTv.text = outcome.data!!.data.tax_id_or_ein_id.toString()
                            }else{
                                binding.taxIdTv.text = "no data"
                            }

                        }

                        //profile status
                        if(outcome.data?.data == null){
                            showCompleteDialog("You have not added your business information ",1)
                        }else if(outcome.data?.data?.profile_completion_status == null){
                            showCompleteDialog("You have not added your business information ",1)
                        }else if(outcome.data?.data?.profile_completion_status?.is_business_info_complete == 0){
                            showCompleteDialog("You have not added your business information",1)
                        }else if(outcome.data?.data?.profile_completion_status?.is_authorize_info_added == 0){
                            showCompleteDialog("You have not added any authorize officer",3)
                        }

                        mGetProfileViewModel.navigationComplete()
                    }else{
                        if(outcome.data?.http_status_code == 401){
                            PrefManager.clearPref()
                            startActivity(Intent(this, ChooseLoginRegActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun showCompleteDialog(content: String, step: Int) {
        val dialog = Dialog(this)
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
                val intent = Intent(this, JobPostActivity::class.java)
                intent.putExtra("step",step)
                startActivity(intent)
            }else{
                dialog.dismiss()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.putExtra("step",step)
                startActivity(intent)
            }
        }
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
}