package com.example.agencyphase2.ui.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.ActivityPaymentDetailsBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.*
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
    private val mUpdatePaymentStatusViewModel: UpdatePaymentStatusViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var amount: String? = null
    private var totalAmount: Int? = null
    private var percentage: Double? = null
    private var job_id: String? = null
    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaymentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            amount = intent?.getStringExtra("amount")!!+".00"
            job_id = intent?.getStringExtra("job_id")!!
            from = intent?.getStringExtra("from")!!
        }

        amount?.let {
            if(amount!!.toDouble() <= 200.00){
                percentage= 20.00
            }else{
                percentage= (amount!!.toDouble()*10).toFloat()/100.00
            }
            val percentList = percentage.toString().split(".").toTypedArray()
            val p1 = percentList[0]
            var p2 = percentList[1]
            if(percentList[1].toDouble()<10.00){
                p2 = percentList[1]+"0"
            }

            val totalList = (amount!!.toFloat()+percentage!!).toString().split(".").toTypedArray()
            val t1 = totalList[0]
            var t2 = totalList[1]
            if(totalList[1].toDouble()<10.00){
                t2 = totalList[1]+"0"
            }

            binding.peaceworcChargeTv.text = "${p1}.${p2}"
            binding.subTotalTv.text = amount.toString()
            binding.totalTv.text = "$${t1}.${t2}"
            binding.payBtn.text = "Pay $${t1}.${t2}"
            totalAmount = ((amount!!.toFloat()+percentage!!)*100).toInt()
        }

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

        binding.payNowBtn.setOnClickListener {
            if(isConnectedToInternet()){
                mGetCustomerIdViewModel.getCustomerId("Bearer $SECRET_KEY")
                loader.show()
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
                        if(!totalAmount.toString().isEmpty()){
                            if(isConnectedToInternet()){
                                mGetPaymentIntentViewModel.getPaymentIntent(customerId,totalAmount.toString(),"usd","true","Bearer $SECRET_KEY")
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
        mUpdatePaymentStatusViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,"Job posted successfully.", Toast.LENGTH_SHORT).show()
                        MainActivity.resume = true
                        if(from == "activity"){
                            finish()
                        }else{
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_SHORT).show()

                }
                is Outcome.Failure<*> -> {
                    Toast.makeText(this,outcome.e.message, Toast.LENGTH_SHORT).show()
                    loader.dismiss()

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause?.message.toString())
                    Log.i("status",outcome.e.message.toString())

                }
            }
        })
    }

    private fun showPaymentFailedDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Failed")
        builder.setMessage("Your Payment has been failed, Please try again to post the job.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            mUpdatePaymentStatusViewModel.updatePaymentStatus(
                job_id!!.toInt(),
                (amount!!.toFloat()+percentage!!).toDouble(),
                customerId.toString(),
                amount!!.toDouble(),
                10,
                percentage!!.toDouble(),
                0,
                accessToken
            )
            loader.show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult?) {

        if (paymentSheetResult is PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful.", Toast.LENGTH_SHORT).show()
            mUpdatePaymentStatusViewModel.updatePaymentStatus(
                job_id!!.toInt(),
                (amount!!.toFloat()+percentage!!).toDouble(),
                customerId.toString(),
                amount!!.toDouble(),
                10,
                percentage!!.toDouble(),
                1,
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