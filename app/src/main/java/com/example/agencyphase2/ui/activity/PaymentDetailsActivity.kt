package com.example.agencyphase2.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.ActivityPaymentDetailsBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCustomerIdViewModel
import com.example.agencyphase2.viewmodel.GetEphemeralKeyViewModel
import com.example.agencyphase2.viewmodel.GetPaymentIntentViewModel
import com.example.agencyphase2.viewmodel.SavePaymentViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentDetailsBinding

    var SECRET_KEY =
        "sk_test_51MQJHfL8ZKWD5NB0RSjiA2UpuyCb9IqZYIuUztJNZKkWH0f4voJn2jcqwJe52YTRtzoqm2kG9bZeVFjoRQyOumEA00n1jYW2B4"
    var PUBLISH_KEY =
        "pk_test_51MQJHfL8ZKWD5NB0rS94Ml3S51XA88c2Aw9GSkFmayOQM3P4ycRFE1NTKwZrhjNi9qodQCoPGe1UwQ1TpnzidFUz009qJ6u5Fj"
    var paymentSheet: PaymentSheet? = null

    var customerId: String? = null
    var ephemeralKey: String? = null
    var clientSecret: String? = null

    private val mGetCustomerIdViewModel: GetCustomerIdViewModel by viewModels()
    private val mGetEphemeralKeyViewModel: GetEphemeralKeyViewModel by viewModels()
    private val mGetPaymentIntentViewModel: GetPaymentIntentViewModel by viewModels()
    private val mSavePaymentViewModel: SavePaymentViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var total_amount: String
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        PaymentConfiguration.init(this, PUBLISH_KEY)
        paymentSheet = PaymentSheet(
            this
        ) { paymentSheetResult: PaymentSheetResult? ->
            onPaymentResult(
                paymentSheetResult
            )
        }

        binding.payBtn.setOnClickListener {
            if(isConnectedToInternet()){
                mGetCustomerIdViewModel.getCustomerId("Bearer ${SECRET_KEY}")
            }else{
                Toast.makeText(this,"No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }

        //observe
        getCustomerIdObserve()
        getEphemeralKeyObserve()
        getPaymentIntentObserve()
        savePaymentObserve()
    }

    private fun getCustomerIdObserve(){
        mGetCustomerIdViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if (outcome.data?.id != null) {
                        customerId = outcome.data?.id
                        if(isConnectedToInternet()){
                            mGetEphemeralKeyViewModel.getEphemeralKey(customerId!!,"Bearer $SECRET_KEY","2020-08-27")
                            loader.show()
                        }else{
                            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                    mGetCustomerIdViewModel.navigationComplete()
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getEphemeralKeyObserve(){
        mGetEphemeralKeyViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if (outcome.data?.id != null) {
                        ephemeralKey = outcome.data?.id
                        var amount = total_amount.toInt()*10
                        if(!amount.toString().isEmpty()){
                            if(isConnectedToInternet()){
                                mGetPaymentIntentViewModel.getPaymentIntent(customerId,amount.toString(),"usd","true","Bearer $SECRET_KEY")
                                loader.show()
                            }else{
                                Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"invalid amount to pay",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    mGetEphemeralKeyViewModel.navigationComplete()
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun getPaymentIntentObserve(){
        mGetPaymentIntentViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if (outcome.data?.id != null) {
                        clientSecret = outcome.data?.client_secret
                        if(isConnectedToInternet()){
                            paymentFlow(customerId,ephemeralKey)
                        }else{
                            Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    mGetPaymentIntentViewModel.navigationComplete()
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun savePaymentObserve(){
        mSavePaymentViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                    mSavePaymentViewModel.navigationComplete()
                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }

    private fun showPaymentFailedDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Failed")
        builder.setMessage("Your Payment has been failed, Please try again to post the job.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Ok, thank you"){dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult?) {

        if (paymentSheetResult is PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful.", Toast.LENGTH_SHORT).show()
            mSavePaymentViewModel.savePayment(
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                accessToken
            )
            loader.show()
        }else{
            Toast.makeText(this, "Woops! Payment Failed.", Toast.LENGTH_SHORT).show()
            showPaymentFailedDialog()
        }
    }

    private fun paymentFlow(customer: String?,ephericalKey: String?) {
        paymentSheet!!.presentWithPaymentIntent(
            clientSecret!!, PaymentSheet.Configuration(
                "peaceworc",
                PaymentSheet.CustomerConfiguration(
                    customer!!,
                    ephericalKey!!
                )
            )
        )
    }
}