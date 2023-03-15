package com.example.agencyphase2.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.databinding.FragmentSettingsBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.AuthActivity
import com.example.agencyphase2.ui.activity.AuthOfficerListActivity
import com.example.agencyphase2.ui.activity.ChangePasswordActivity
import com.example.agencyphase2.ui.activity.ChooseLoginRegActivity
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetAuthorizeOfficerViewModel
import com.example.agencyphase2.viewmodel.LoginViewModel
import com.example.agencyphase2.viewmodel.LogoutViewModel
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogoutViewModel by viewModels()
    private val mGetAuthorizeOfficerViewModel: GetAuthorizeOfficerViewModel by viewModels()

    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        binding.logoutBtn.setOnClickListener {
            showLogoutPopUp()
        }

        binding.changePassBtn.setOnClickListener {
            val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.authorizeOfficerBtn.setOnClickListener {
            val intent = Intent(requireActivity(), AuthOfficerListActivity::class.java)
            startActivity(intent)
        }

        //observe
        logoutObserve()
        getAuthorizeInoObserve()

        binding.ownerMainView.gone()
        binding.ownerShimmerView.visible()
        binding.ownerShimmerView.startShimmer()
        if(requireActivity().isConnectedToInternet()){
            mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
        }else{
            Toast.makeText(requireActivity(),"Oops!! No internet connection.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutPopUp(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Logout")
        builder.setMessage("Do you want to exit the app ?")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(requireActivity().isConnectedToInternet()){
                logoutViewModel.logout(accessToken)
                loader = requireActivity().loadingDialog()
                loader.show()
            }else{
                Toast.makeText(requireActivity(),"No internet connection.",Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun logoutObserve(){
        logoutViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        PrefManager.clearPref()
                        startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                        requireActivity().finish()
                        logoutViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        PrefManager.clearPref()
                        startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                        requireActivity().finish()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()

                    PrefManager.clearPref()
                    startActivity(Intent(requireActivity(), ChooseLoginRegActivity::class.java))
                    requireActivity().finish()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getAuthorizeInoObserve(){
        mGetAuthorizeOfficerViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    binding.ownerMainView.visible()
                    binding.ownerShimmerView.gone()
                    binding.ownerShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            outcome.data?.data!![0].name?.let {
                                binding.fullNameTv.text = outcome.data?.data!![0].name
                            }
                            outcome.data?.data!![0].email?.let {
                                binding.emailTv.text = outcome.data?.data!![0].email
                            }
                            outcome.data?.data!![0].role?.let {
                                binding.roleTv.text = outcome.data?.data!![0].role
                            }
                            outcome.data?.data!![0].phone?.let {
                                binding.mobileTv.text = it.toString()
                            }
                        }else{
                            binding.fullNameTv.text = "No Data"
                            binding.emailTv.text = "No Data"
                            binding.roleTv.text = "No Data"
                            binding.mobileTv.text = "No Data     "
                        }
                        mGetAuthorizeOfficerViewModel.navigationComplete()
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