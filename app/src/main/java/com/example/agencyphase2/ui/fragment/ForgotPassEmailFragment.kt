package com.example.agencyphase2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.FragmentForgotPassEmailBinding
import com.example.agencyphase2.databinding.FragmentLoginBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.viewmodel.ForgotPassEmailViewModel
import com.example.agencyphase2.viewmodel.GetCompleteJobViewModel
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPassEmailFragment : Fragment() {
    private var _binding: FragmentForgotPassEmailBinding? = null
    private val binding get() = _binding!!

    private val mForgotPassEmailViewModel: ForgotPassEmailViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPassEmailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //validation
        emailFocusListener()

        //observer
        sendEmailObserve()

        loader = requireActivity().loadingDialog()

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.loginBtn.setOnClickListener {
            val validEmail = binding.emailTxtLay.helperText == null && binding.emailTxt.text.toString().isNotEmpty()
            if(validEmail){
                requireActivity().hideSoftKeyboard()
                if(requireActivity().isConnectedToInternet()){
                    mForgotPassEmailViewModel.sendEmail(
                        binding.emailTxt.text.toString()
                    )
                    loader.show()
                }else{
                    Toast.makeText(requireActivity(),"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(binding.emailTxtLay.helperText == null) binding.emailTxtLay.helperText = "required"
                Toast.makeText(requireActivity(),binding.emailTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                binding.emailTxt.showKeyboard()
            }
        }
    }

    private fun emailFocusListener(){
        binding.emailTxt.doOnTextChanged { text, start, before, count ->
            binding.emailTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }

        return null
    }

    private fun sendEmailObserve(){
        mForgotPassEmailViewModel.response.observe(viewLifecycleOwner, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        val bundle = Bundle()
                        bundle.putString("email", binding.emailTxt.text.toString())
                        findNavController().navigate(
                            R.id.action_forgotPassEmailFragment_to_forgotPasswordOTPFragment,
                            bundle
                        )
                        mForgotPassEmailViewModel.navigationComplete()
                    }else{
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(requireActivity(),outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

}