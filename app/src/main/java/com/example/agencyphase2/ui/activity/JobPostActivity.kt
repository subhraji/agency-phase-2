package com.example.agencyphase2.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ChipsAdapter
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.GetCustomerIdViewModel
import com.example.agencyphase2.viewmodel.GetEphemeralKeyViewModel
import com.example.agencyphase2.viewmodel.GetPaymentIntentViewModel
import com.example.agencyphase2.viewmodel.PostJobViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class JobPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobPostBinding
    var careType:String = ""
    var date:String = ""
    var startTime:String = ""
    var endTime:String = ""
    var job_address: String = ""
    var place_name: String = ""
    var lat: String = ""
    var lang: String = ""

    private var durationHour: Int = 0
    private var durationDay: Int = 0
    private var durationTotalMin: Int = 0

    private val medicalHistoryList:MutableList<String> = mutableListOf()
    private val jobSkillList:MutableList<String> = mutableListOf()
    private val otherReqList:MutableList<String> = mutableListOf()
    private val checkList:MutableList<String> = mutableListOf()

    private lateinit var accessToken: String

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    var SECRET_KEY =
        "sk_test_51MQJHfL8ZKWD5NB0RSjiA2UpuyCb9IqZYIuUztJNZKkWH0f4voJn2jcqwJe52YTRtzoqm2kG9bZeVFjoRQyOumEA00n1jYW2B4"
    var PUBLISH_KEY =
        "pk_test_51MQJHfL8ZKWD5NB0rS94Ml3S51XA88c2Aw9GSkFmayOQM3P4ycRFE1NTKwZrhjNi9qodQCoPGe1UwQ1TpnzidFUz009qJ6u5Fj"
    var paymentSheet: PaymentSheet? = null

    var customerId: String? = null
    var ephemeralKey: String? = null
    var clientSecret: String? = null

    companion object {
        var genderAgeList: MutableList<GenderAgeItemCountModel> = mutableListOf()
    }

    //viewmodel
    private val mGetCustomerIdViewModel: GetCustomerIdViewModel by viewModels()
    private val mGetEphemeralKeyViewModel: GetEphemeralKeyViewModel by viewModels()
    private val mGetPaymentIntentViewModel: GetPaymentIntentViewModel by viewModels()
    private val mPostJobViewModel: PostJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        PaymentConfiguration.init(this, PUBLISH_KEY)

        paymentSheet = PaymentSheet(
            this
        ) { paymentSheetResult: PaymentSheetResult? ->
            onPaymentResult(
                paymentSheetResult
            )
        }

        Places.initialize(applicationContext, getString(R.string.api_key))

        //binding.relativeLay1.gone()
        binding.relativeLay2.gone()
        binding.relativeLay3.gone()
        binding.dateTimeLay.gone()
        loader = this.loadingDialog()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //initialize page
        initializePageOne()
        initializePageTwo()
        initializePageThree()

        //observer
        jobPostObserve()
        getCustomerIdObserve()
        getEphemeralKeyObserve()
        getPaymentIntentObserve()

    }

    private fun autocompleteWithIntent(){
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLng(26.1442464,91.784392),
                    LatLng(26.1442464,91.784392)
                )
            )
            .setCountry("IN")
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun showCareTypeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.care_type_bottomsheet, null)

        val btnSave = view.findViewById<CardView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val careTypeRbg = view.findViewById<RadioGroup>(R.id.type_of_care_rbg)
        val ageTxt = view.findViewById<EditText>(R.id.age_txt)
        val nameTxt = view.findViewById<EditText>(R.id.name_txt)
        val seniorLay = view.findViewById<LinearLayout>(R.id.senior_lay)
        val childLay = view.findViewById<LinearLayout>(R.id.child_lay)
        val patientLay = view.findViewById<LinearLayout>(R.id.patient_lay)
        val seniorImg = view.findViewById<ImageView>(R.id.senior_img)
        val childImg = view.findViewById<ImageView>(R.id.child_img)
        val patientImg = view.findViewById<ImageView>(R.id.patient_img)
        val seniorTv = view.findViewById<TextView>(R.id.senior_tv)
        val childTv = view.findViewById<TextView>(R.id.child_tv)
        val patientTv = view.findViewById<TextView>(R.id.patient_tv)

        //care type select
        seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
        childTv.setTextColor(resources.getColor(R.color.text_grey, null))
        patientTv.setTextColor(resources.getColor(R.color.text_grey, null))

        var gender: String = ""
        careTypeRbg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb -> {
                    gender = "male"
                }
                R.id.female_rb -> {
                    gender = "female"
                }
                R.id.others_rb -> {
                    gender = "Others"
                }
            }
        })

        btnClear.setOnClickListener {
            genderAgeList = mutableListOf()
            careType = ""
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val age = ageTxt.text.toString()
            val name = nameTxt.text.toString()
            if(!careType.isEmpty()){
                if(!name.isEmpty()){
                    if(!age.isEmpty()){
                        if(!gender.isEmpty()){
                            genderAgeList.add(GenderAgeItemCountModel(careType,gender,age,name))
                            fillGenderAgeRecycler(genderAgeList, binding.careTypeRecycler)
                            fillGenderAgeRecycler(genderAgeList, binding.showGenderAgeRecycler)
                            dialog.dismiss()
                        }else{
                            Toast.makeText(this,"Please select gender.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        ageTxt.showKeyboard()
                        Toast.makeText(this,"Please provide the age of the patient", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    nameTxt.showKeyboard()
                    Toast.makeText(this,"Please type the name of the patient", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please select a care type", Toast.LENGTH_SHORT).show()
            }

        }

        //care type
        seniorLay.setOnClickListener {
            seniorLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            seniorTv.setTextColor(resources.getColor(R.color.white, null))
            childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "senior care"
        }
        childLay.setOnClickListener {
            seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            childLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            childImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            childTv.setTextColor(resources.getColor(R.color.white, null))
            patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "child care"
        }
        patientLay.setOnClickListener {
            seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            patientLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            patientImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)

            seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            patientTv.setTextColor(resources.getColor(R.color.white, null))
            careType = "patient care"
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
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

        datePicker.setMinDate(System.currentTimeMillis() - 1000)

        btnClear.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            binding.timeTv1.text = startTime+" - "+endTime
            binding.dateTv1.text = getCurrentDate(datePicker)
            binding.showDateTv.text = getCurrentDate(datePicker)
            binding.showTimeTv.text = startTime+" - "+endTime
            date = getCurrentDate(datePicker).toString()
            binding.dateTimeBtn.gone()
            binding.dateTimeLay.visible()
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
                    //startTime = "$selectedHour-$selectedMinute-${"00"}"

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
                    //endTime = "$selectedHour-$selectedMinute-${"00"}"

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
        var month = ""
        var day = ""
        if((picker.getMonth() + 1) < 10){
            month = "0"+ (picker.getMonth() + 1)
        }else{
            month = (picker.getMonth() + 1).toString()
        }
        if(picker.getDayOfMonth() < 10){
            day = "0"+picker.getDayOfMonth().toString()
        }else{
            day = picker.getDayOfMonth().toString()
        }

        builder.append(month.toString() + "-") //month is 0 based
        builder.append(day.toString() + "-")
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
                        mPostJobViewModel.navigationComplete()
                        finish()
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

    private fun initializePageOne(){
        binding.dateTimeBtn.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.dateTimeLay.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.consLay2.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.careTypeBtn.setOnClickListener {
            /*val intent = Intent(this, SelectCareTypeActivity::class.java)
            startActivity(intent)*/
            showCareTypeBottomSheet()
        }

        binding.jobLocBtn.setOnClickListener {
            /*val intent = Intent(this, SearchLocationActivity::class.java)
            startActivity(intent)*/
            autocompleteWithIntent()
        }

        binding.requiredNextStepBtn.setOnClickListener {
            val job_title = binding.jobTitleTxt.text.toString()
            val amount = binding.addAmountTxt.text.toString()
            val job_desc = binding.jobDescTxt.text.toString()

            if(!job_title.isEmpty()){
                if(!careType.isEmpty()){
                    if(!date.isEmpty()){
                        if(!startTime.isEmpty()){
                            if(!endTime.isEmpty()){
                                if(!amount.isEmpty()){
                                    if(!job_address.isEmpty()){
                                        if(!job_desc.isEmpty()){
                                            binding.relativeLay1.gone()
                                            binding.relativeLay3.gone()
                                            binding.relativeLay2.visible()
                                        }else{
                                            Toast.makeText(this,"Please provide the job description.",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(this,"Please provide the address of this job.",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"Please type a remittance.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"Please select a end time for this job.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"Please select a start time for this job.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Please select a date for this job.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Please select a care type.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please provide the job title.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializePageTwo(){
        binding.addMhBtn.setOnClickListener {
            val medicalHistoryTxt = binding.medicalHistoryTxt.text.toString()
            if(!medicalHistoryTxt.isEmpty()){
                medicalHistoryList.add(medicalHistoryTxt)
                fillMedicalRecycler(medicalHistoryList, binding.medicalRecycler)
                binding.medicalHistoryTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.medicalHistoryTxt.showKeyboard()
            }
        }

        binding.addJsBtn.setOnClickListener {
            val jobSkillTxt = binding.jobSkillTxt.text.toString()
            if(!jobSkillTxt.isEmpty()){
                jobSkillList.add(jobSkillTxt)
                fillJobSkillRecycler(jobSkillList, binding.expertiseRecycler)
                binding.jobSkillTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.jobSkillTxt.showKeyboard()
            }
        }

        binding.addOrBtn.setOnClickListener {
            val otherReqTxt = binding.otherRequirementsTxt.text.toString()
            if(!otherReqTxt.isEmpty()){
                otherReqList.add(otherReqTxt)
                fillOtherRequirementRecycler(otherReqList, binding.otherRequirementRecycler)
                binding.otherRequirementsTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.otherRequirementsTxt.showKeyboard()
            }
        }

        binding.addCheckListBtn.setOnClickListener {
            val checkListTxt = binding.checklistTxt.text.toString()
            if(!checkListTxt.isEmpty()){
                checkList.add(checkListTxt)
                fillCheckListRecycler(checkList, binding.checklistRecycler)
                binding.checklistTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.checklistTxt.showKeyboard()
            }
        }

        binding.optionalNextStepBtn.setOnClickListener {
            binding.relativeLay2.gone()
            binding.relativeLay1.gone()
            binding.relativeLay3.visible()
            binding.showJobTitleTxt.text = Editable.Factory.getInstance().newEditable(binding.jobTitleTxt.text.toString())
            binding.showAmountTxt.text = Editable.Factory.getInstance().newEditable(binding.addAmountTxt.text.toString())

            //binding.showAddressTxt.text = Editable.Factory.getInstance().newEditable(binding.jobLocTxt.text.toString())
            binding.showAddressTxt.text = Editable.Factory.getInstance().newEditable(job_address)

            binding.showJobDescTxt.text = Editable.Factory.getInstance().newEditable(binding.jobDescTxt.text.toString())
        }
    }

    private fun initializePageThree() {

        binding.addBtnMedi.setOnClickListener {
            val medicalHistoryTxt = binding.prevMedicalHistoryTxt.text.toString()
            if(!medicalHistoryTxt.isEmpty()){
                medicalHistoryList.add(medicalHistoryTxt)
                fillMedicalRecycler(medicalHistoryList, binding.showMedicalHisRecycler)
                binding.prevMedicalHistoryTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.prevMedicalHistoryTxt.showKeyboard()
            }
        }

        binding.addBtnSkill.setOnClickListener {
            val jobSkillTxt = binding.prevJobSkillTxt.text.toString()
            if(!jobSkillTxt.isEmpty()){
                jobSkillList.add(jobSkillTxt)
                fillJobSkillRecycler(jobSkillList, binding.showJobSkillRecycler)
                binding.prevJobSkillTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.prevJobSkillTxt.showKeyboard()
            }
        }

        binding.prevAddOrBtn.setOnClickListener {
            val otherReqTxt = binding.prevAddOrTxt.text.toString()
            if(!otherReqTxt.isEmpty()){
                otherReqList.add(otherReqTxt)
                fillOtherRequirementRecycler(otherReqList, binding.showOtherRequirementsRecycler)
                binding.prevAddOrTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.prevAddOrTxt.showKeyboard()
            }
        }

        binding.addBtnCheck.setOnClickListener {
            val checkListTxt = binding.prevChecklistTxt.text.toString()
            if(!checkListTxt.isEmpty()){
                checkList.add(checkListTxt)
                fillCheckListRecycler(checkList, binding.showCheckListRecycler)
                binding.prevChecklistTxt.text = null
            }else{
                Toast.makeText(this,"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
                binding.prevChecklistTxt.showKeyboard()
            }
        }

        binding.consLay1.setOnClickListener {
            val intent = Intent(this, SelectCareTypeActivity::class.java)
            startActivity(intent)
        }

        binding.prevNextStepBtn.setOnClickListener {
            mGetCustomerIdViewModel.getCustomerId("Bearer ${SECRET_KEY}")
            loader.show()
        }

        fillMedicalRecycler(medicalHistoryList, binding.showMedicalHisRecycler)
        fillJobSkillRecycler(jobSkillList, binding.showJobSkillRecycler)
        fillOtherRequirementRecycler(otherReqList, binding.showOtherRequirementsRecycler)
        fillCheckListRecycler(checkList, binding.showCheckListRecycler)
    }

    private fun fillGenderAgeRecycler(list: List<GenderAgeItemCountModel>, recycleView: RecyclerView) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycleView.apply {
            layoutManager = gridLayoutManager
            adapter = GenderAgeAdapter(list.toMutableList(),this@JobPostActivity, careType)
        }
    }

    private fun fillMedicalRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(this)
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,this@JobPostActivity)
        }
    }

    private fun fillJobSkillRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(this)
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,this@JobPostActivity)
        }
    }

    private fun fillOtherRequirementRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(this)
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,this@JobPostActivity)
        }
    }

    private fun fillCheckListRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(this)
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,this@JobPostActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("place", "Place: ${place.name}, ${place.id}, ${place.latLng}")
                        binding.jobLocTxt.text = place.address
                        job_address = place.address
                        place_name = place.name

                        lat = place.latLng.toString()
                        lang = place.latLng.toString()

                        /*val latLangList: MutableList<List<String>> = Arrays.asList(place.latLng.toString().split(","))
                        lat = latLangList[0].toString()
                        lang = latLangList[1].toString()*/
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("place", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //stripe
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
                        var amount = binding.showAmountTxt.text.toString().toInt()*100
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
            if(isConnectedToInternet()){

                mPostJobViewModel.jobPost(
                    binding.showJobTitleTxt.text.toString(),
                    binding.showCareTypeTv.text.toString(),
                    genderAgeList,
                    date,
                    startTime,
                    endTime,
                    binding.showAmountTxt.text.toString(),
                    binding.showAddressTxt.text.toString(),
                    binding.showJobDescTxt.text.toString(),
                    medicalHistoryList,
                    jobSkillList,
                    otherReqList,
                    checkList,
                    place_name,
                    lat,
                    lang,
                    accessToken
                )
                loader.show()

            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
            }
            binding.prevNextStepBtn.gone()

        }else{
            Toast.makeText(this, "Woops! Payment Failed.", Toast.LENGTH_SHORT).show()
            showPaymentFailedDialog()
        }
    }

    override fun onDestroy() {
        genderAgeList = mutableListOf()
        super.onDestroy()
    }


    private fun getDurationHour(startDateTime: String, endDateTime: String) {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        try {
            val d1:Date = sdf.parse(startDateTime)
            val d2:Date = sdf.parse(endDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            durationDay = difference_In_Days.toInt()
            durationHour = difference_In_Hours.toInt()

            durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()

            if(durationHour < 0){
                val endDateTimeArray = endDateTime.split(" ")
                val date = endDateTimeArray[0]
                val time = endDateTimeArray[1]

                val dateArray = date.split("-")
                val day = dateArray[0].toInt()
                val month = dateArray[1].toInt()
                val year = dateArray[2].toInt()

                val datePlus1: LocalDate = LocalDate.of(year, month, day).plusDays(1)
                val datePlus1Array = datePlus1.toString().split("-")
                val new_day = datePlus1Array[2]
                val new_month = datePlus1Array[1]
                val new_year = datePlus1Array[0]
                val new_end_date = new_day+"-"+new_month+"-"+new_year
                val new_end_date_time = new_end_date+" "+time
                Log.d("dateTime", "new year => "+new_end_date_time.toString())

                getDurationHour(startDateTime,new_end_date_time)

            }


            Log.d("dateTime","duration => "+
                    difference_In_Years.toString()+
                    " years, "
                    + difference_In_Days
                    + " days, "
                    + difference_In_Hours
                    + " hours, "
                    + difference_In_Minutes
                    + " minutes, "
                    + difference_In_Seconds
                    + " seconds"
            )
        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }
    }

}