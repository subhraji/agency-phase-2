package com.example.agencyphase2.ui.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.PostJobViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.user.caregiver.gone
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import java.util.*

@AndroidEntryPoint
class JobPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobPostBinding
    val careTypeList: Array<String> =  arrayOf("Type of care", "Child care", "Senior care", "Patient care")
    var careType:String = ""
    var gender:String = ""
    var age:String = ""
    var date: String = ""
    var startTime: String = ""
    var endTime: String = ""
    private lateinit var accessToken: String

    //lists
    companion object {
        var genderAgeList: MutableList<GenderAgeItemCountModel> = mutableListOf()
    }

    //viewmodel
    private val mPostJobViewModel: PostJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.relativeLay2.gone()
        binding.relativeLay3.gone()

        binding.dateTimeBtn.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.requiredNextStepBtn.setOnClickListener {
            /*mPostJobViewModel.jobPost(
                binding.jobTitleTxt.text.toString(),
                careType,
                genderAgeList,
                date,
                startTime,
                endTime,
                binding.addAmountTxt.text.toString(),
                binding.jobLocTxt.text.toString(),
                binding.jobDescTxt.text.toString(),
                accessToken
            )
            loader = this.loadingDialog()
            loader.show()*/

            binding.relativeLay1.gone()
            binding.relativeLay3.gone()
            binding.relativeLay2.visible()
        }

        binding.optionalNextStepBtn.setOnClickListener {
            binding.relativeLay2.gone()
            binding.relativeLay1.gone()
            binding.relativeLay3.visible()
        }

        binding.prevNextStepBtn.setOnClickListener {
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.careTypeBtn.setOnClickListener {
            val intent = Intent(this, SelectCareTypeActivity::class.java)
            startActivity(intent)
        }

        //observer
        jobPostObserve()
    }

    override fun onResume() {
        Log.d("genderAge", genderAgeList.toString())
        fillGenderAgeRecycler(genderAgeList)
        super.onResume()
    }

    private fun showCareTypeBottomSheet(care: String){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.care_type_bottomsheet, null)

        val btnSave = view.findViewById<CardView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val childTv = view.findViewById<TextView>(R.id.child_tv)
        val seniorTv = view.findViewById<TextView>(R.id.senior_tv)
        val patientTv = view.findViewById<TextView>(R.id.patient_tv)
        val careTypeRbg = view.findViewById<RadioGroup>(R.id.type_of_care_rbg)
        val ageTxt = view.findViewById<EditText>(R.id.age_txt)

        if(care == "Child care"){
            childTv.setBackgroundColor(Color.parseColor("#000000"));
            seniorTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            patientTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            childTv.setTextColor(Color.parseColor("#ffffff"));

        }else if(care == "Senior care"){
            seniorTv.setBackgroundColor(Color.parseColor("#000000"));
            childTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            patientTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            seniorTv.setTextColor(Color.parseColor("#ffffff"));

        }else if(care == "Patient care"){
            patientTv.setBackgroundColor(Color.parseColor("#000000"));
            childTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            seniorTv.setBackgroundColor(Color.parseColor("#E3E3E3"));
            patientTv.setTextColor(Color.parseColor("#ffffff"));
        }

        careTypeRbg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb -> {
                    gender = "male"
                }
                R.id.female_rb -> {
                    gender = "female"
                }
            }
        })

        btnClear.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            age = ageTxt.text.toString()
            genderAgeList.add(GenderAgeItemCountModel(gender,age))
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun fillGenderAgeRecycler(list: List<GenderAgeItemCountModel>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.genderAgeRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = GenderAgeAdapter(list,this@JobPostActivity, careType)
        }
    }

    private fun showDateTimeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_date_time_bottomsheet, null)

        val btnSave = view.findViewById<TextView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
        val startTimeBtn = view.findViewById<RelativeLayout>(R.id.start_time_btn)
        val endTimeBtn = view.findViewById<RelativeLayout>(R.id.end_time_btn)
        val startTimeTv = view.findViewById<TextView>(R.id.start_time_txt)
        val endTimeTv = view.findViewById<TextView>(R.id.end_time_txt)

        btnClear.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            binding.timeDateTxt.text = getCurrentDate(datePicker)
            date = getCurrentDate(datePicker).toString()
            dialog.dismiss()
        }

        startTimeBtn.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(this,
                { timePicker, selectedHour, selectedMinute ->
                    var AM_PM: String
                    var hour2: Int
                    hour2 = selectedHour
                    if (selectedHour>12){
                        hour2 -= 12
                        AM_PM=" PM";
                    }else if(selectedHour == 0){
                        hour2 += 12
                        AM_PM=" AM";
                    }else if(selectedHour == 12){
                        AM_PM=" PM";
                    }else {
                        AM_PM=" AM";
                    }
                    if(selectedMinute < 10){
                        startTimeTv.setText("$hour2:0$selectedMinute ${AM_PM}")
                    }else{
                        startTimeTv.setText("$hour2:$selectedMinute ${AM_PM}")
                    }
                    startTime = "$selectedHour:$selectedMinute:${"00"}"
                },
                hour,
                minute,
                false
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Start Time")
            mTimePicker.show()
        }

        endTimeBtn.setOnClickListener {
            val mcurrentTime: Calendar = Calendar.getInstance()
            val hour: Int = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute: Int = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(this,
                { timePicker, selectedHour, selectedMinute ->
                    var AM_PM: String
                    var hour2: Int
                    hour2 = selectedHour
                    if (selectedHour>12){
                        hour2 -= 12
                        AM_PM=" PM";
                    }else if(selectedHour == 0){
                        hour2 += 12
                        AM_PM=" AM";
                    }else if(selectedHour == 12){
                        AM_PM=" PM";
                    }else {
                        AM_PM=" AM";
                    }
                    if(selectedMinute<10){
                        endTimeTv.setText("$hour2:0$selectedMinute ${AM_PM}")
                    }else{
                        endTimeTv.setText("$hour2:$selectedMinute ${AM_PM}")
                    }
                    endTime = "$selectedHour:$selectedMinute:${"00"}"
                },
                hour,
                minute,
                false
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select End Time")
            mTimePicker.show()
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    fun getCurrentDate(picker: DatePicker): String? {
        val builder = StringBuilder()
        builder.append((picker.getMonth() + 1).toString() + "/") //month is 0 based
        builder.append(picker.getDayOfMonth().toString() + "/")
        builder.append(picker.getYear())
        return builder.toString()
    }

    private fun jobPostObserve(){
        mPostJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, PostJobPreviewActivity::class.java)
                        startActivity(intent)
                        mPostJobViewModel.navigationComplete()

                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        genderAgeList = mutableListOf()
        super.onDestroy()
    }

}