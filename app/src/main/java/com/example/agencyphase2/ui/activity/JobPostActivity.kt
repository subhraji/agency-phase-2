package com.example.agencyphase2.ui.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.agencyphase2.viewmodel.PostJobViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class JobPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobPostBinding
    var careType:String = ""
    var start_date:String = ""
    var end_date:String = ""

    var startDate = ""
    var endDate = ""
    var startTime:String = ""
    var endTime:String = ""
    var job_address: String = ""
    var place_name: String = ""
    var lat: String = ""
    var lang: String = ""

    var street_n: String = ""
    var city_n: String = ""
    var state_n: String = ""
    var zipcode_n: String = ""
    var building_n: String? = null
    var floor_n: String? = null
    var country_n: String? = null

    private var durationHour: Int = 0
    private var durationDay: Int = 0
    private var durationTotalMin: Int = 0

    private val medicalHistoryList:MutableList<String> = mutableListOf()
    private val jobSkillList:MutableList<String> = mutableListOf()
    private val otherReqList:MutableList<String> = mutableListOf()
    private val checkList:MutableList<String> = mutableListOf()

    private lateinit var accessToken: String

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    companion object {
        var genderAgeList: MutableList<GenderAgeItemCountModel> = mutableListOf()
    }

    private val mPostJobViewModel: PostJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityJobPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        Places.initialize(applicationContext, getString(R.string.api_key))
        autocomplete()
        showAutocomplete()

        //binding.relativeLay1.visible()
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
    }

    private fun autocomplete(){
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        val etTextInput: EditText = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etTextInput.setTextColor(R.color.black)
        etTextInput.setTextSize(14.5f)
        etTextInput.setHint(R.string.search_loc)
        etTextInput.setHintTextColor(R.color.black)

        /*val ivSearch: ImageView = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button)
        ivSearch.setImageResource(R.drawable.ic_gps_19)*/

        //autocompleteFragment.setTypeFilter(TypeFilter.Ad)
        autocompleteFragment.setCountries("US")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                job_address = place.address
                place_name = place.name

                val latLangList = place.latLng.toString().split("(").toTypedArray()
                val final_latLangList = latLangList[1].toString().split(",").toTypedArray()
                lat = final_latLangList[0].toString()
                lang = final_latLangList[1].toString().substring(0, final_latLangList[1].length - 1)

                var streetName = ""
                var streetNumber = ""
                var city = ""
                var state = ""
                var zipcode = ""

                for (i in place.addressComponents.asList()){
                    if(i.types[0] == "locality"){
                        city = i.name
                    }
                    if(i.types[0] == "route"){
                        streetName = i.name.toString()
                    }
                    if(i.types[0] == "street_number"){
                        streetNumber = i.name.toString()
                    }
                    if(i.types[0] == "administrative_area_level_1"){
                        state = i.name.toString()
                    }
                    if(i.types[0] == "postal_code"){
                        zipcode = i.name.toString()
                    }
                }

                showAddressBottomSheet(place_name, streetName, streetNumber, city, state, zipcode)

            }

            override fun onError(status: Status) {
                Log.i("place2", "An error occurred: $status")
            }
        })
    }

    private fun showAutocomplete(){
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.show_autocomplete_fragment) as AutocompleteSupportFragment

        val etTextInput: EditText = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etTextInput.setTextColor(R.color.black)
        etTextInput.setTextSize(14.5f)
        etTextInput.setHint(R.string.search_loc)
        etTextInput.setHintTextColor(R.color.black)

        /*val ivSearch: ImageView = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button)
        ivSearch.setImageResource(R.drawable.ic_gps_19)*/

        //autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment.setCountries("US")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                job_address = place.address
                place_name = place.name

                val latLangList = place.latLng.toString().split("(").toTypedArray()
                val final_latLangList = latLangList[1].toString().split(",").toTypedArray()
                lat = final_latLangList[0].toString()
                lang = final_latLangList[1].toString().substring(0, final_latLangList[1].length - 1)

                var streetName = ""
                var streetNumber = ""
                var city = ""
                var state = ""
                var zipcode = ""

                for (i in place.addressComponents.asList()){
                    if(i.types[0] == "locality"){
                        city = i.name
                    }
                    if(i.types[0] == "route"){
                        streetName = i.name.toString()
                    }
                    if(i.types[0] == "street_number"){
                        streetNumber = i.name.toString()
                    }
                    if(i.types[0] == "administrative_area_level_1"){
                        state = i.name.toString()
                    }
                    if(i.types[0] == "postal_code"){
                        zipcode = i.name.toString()
                    }
                }

                showAddressBottomSheet(place_name, streetName, streetNumber, city, state, zipcode)

            }

            override fun onError(status: Status) {
                Log.i("place2", "An error occurred: $status")
            }
        })
    }

    private fun showCareTypeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.care_type_bottomsheet_layout, null)

        val btnSave = view.findViewById<CardView>(R.id.save_care_type_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_care_type_btn)
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

        btnClear.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            if(!careType.isEmpty()){
                binding.addPatient.visible()
                binding.addCareTypeHtv.text = careType.toString()
                binding.showAddCareTypeHtv.text = careType.toString()
                dialog.dismiss()
            }else{
                careType = ""
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
            binding.careTypeImg1.setImageResource(R.drawable.ic_senior_49)
            binding.careTypeImg2.setImageResource(R.drawable.ic_senior_49)

            careType = "Senior Care"
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
            binding.careTypeImg1.setImageResource(R.drawable.ic_childe_care_49)
            binding.careTypeImg2.setImageResource(R.drawable.ic_childe_care_49)
            careType = "Child Care"
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
            binding.careTypeImg1.setImageResource(R.drawable.ic_patient_care_49)
            binding.careTypeImg2.setImageResource(R.drawable.ic_patient_care_49)
            careType = "Patient Care"
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showPatientDetailsBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.care_type_bottomsheet, null)

        val btnSave = view.findViewById<CardView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val careTypeRbg = view.findViewById<RadioGroup>(R.id.type_of_care_rbg)
        val ageTxt = view.findViewById<EditText>(R.id.age_txt)
        val nameTxt = view.findViewById<EditText>(R.id.name_txt)

        var gender: String = ""
        careTypeRbg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb -> {
                    gender = "Male"
                }
                R.id.female_rb -> {
                    gender = "Female"
                }
                R.id.others_rb -> {
                    gender = "Others"
                }
            }
        })

        btnClear.setOnClickListener {
            //genderAgeList = mutableListOf()
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val age = ageTxt.text.toString()
            val name = nameTxt.text.toString()
            if(!name.isEmpty()){
                if(!age.isEmpty()){
                    if(!gender.isEmpty()){
                        if(genderAgeList.size <= 5){
                            genderAgeList.add(GenderAgeItemCountModel(gender,age,name))
                            fillGenderAgeRecycler(genderAgeList, binding.careTypeRecycler)
                            fillGenderAgeRecycler(genderAgeList, binding.showGenderAgeRecycler)
                            dialog.dismiss()
                        }else{
                            Toast.makeText(this,"You can not add more than 5 patient.", Toast.LENGTH_SHORT).show()
                        }
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

        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showDateTimeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_date_time_bottomsheet, null)

        val conLay_1 = view.findViewById<ConstraintLayout>(R.id.cons_lay_1)
        val btnNext = view.findViewById<TextView>(R.id.next_btn)
        val btnClear_1 = view.findViewById<ImageView>(R.id.clear_btn_1)
        val startTimeBtn = view.findViewById<RelativeLayout>(R.id.start_time_btn)
        val startTimeTv = view.findViewById<TextView>(R.id.start_time_txt)
        val startDatePicker = view.findViewById<DatePicker>(R.id.start_date_picker)
        val conLay_2 = view.findViewById<ConstraintLayout>(R.id.cons_lay_2)
        val btnSave = view.findViewById<TextView>(R.id.save_btn)
        val btnClear_2 = view.findViewById<ImageView>(R.id.clear_btn_2)
        val endTimeBtn = view.findViewById<RelativeLayout>(R.id.end_time_btn)
        val endTimeTv = view.findViewById<TextView>(R.id.end_time_txt)
        val endDatePicker = view.findViewById<DatePicker>(R.id.end_date_picker)

        startDatePicker.setMinDate(System.currentTimeMillis() - 1000)
        endDatePicker.setMinDate(System.currentTimeMillis() - 1000)
        conLay_2.gone()
        conLay_1.visible()

        var startTime_n = ""
        var endTime_n = ""

        btnClear_1.setOnClickListener {
            dialog.dismiss()
        }
        btnClear_2.setOnClickListener {
            dialog.dismiss()
        }
        btnNext.setOnClickListener {

            start_date = getDate(startDatePicker).toString()
            startDate = getDate2(startDatePicker).toString()
            if(!start_date.isEmpty()){
                if(!startTime.isEmpty()){
                    conLay_2.visible()
                    conLay_1.gone()
                }else{
                    Toast.makeText(this,"Please select start time.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please select start date.", Toast.LENGTH_SHORT).show()
            }
        }
        btnSave.setOnClickListener {
            end_date = getDate(endDatePicker).toString()
            endDate = getDate2(startDatePicker).toString()

            if(!startTime.isEmpty()){
                if(!endTime.isEmpty()){
                    if(!startDate.isEmpty()){
                        if(!endDate.isEmpty()){
                              if(getDurationHour(
                                    getCurrentDate(),
                                    startDate+" "+startTime
                                ) >= 180){
                                if(getDurationHour(
                                        startDate+" "+startTime,
                                        endDate+" "+endTime
                                    ) >= 60){

                                    binding.dateTv1.text = start_date+" to "+end_date
                                    binding.showDateTv.text = start_date+" to "+end_date
                                    binding.timeTv1.text = startTime_n+" - "+endTime_n
                                    binding.showTimeTv.text = startTime_n+" - "+endTime_n
                                    binding.dateTimeBtn.gone()
                                    binding.dateTimeLay.visible()
                                    dialog.dismiss()

                                }else{
                                    if(
                                        getDurationHour(
                                            startDate+" "+startTime,
                                            endDate+" "+endTime
                                        ) > 0
                                    ){
                                        Toast.makeText(this,"Job duration should be more than 1 hour  ${getDurationHour(
                                            startDate+" "+startTime,
                                            endDate+" "+endTime
                                        )}", Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(this,"Please check the end time", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }else{
                                Toast.makeText(this,"There should be a minimum of 3 hours gap current time and job start time.", Toast.LENGTH_LONG).show()
                            }
                        }else{
                            Toast.makeText(this,"Please provide end date.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Please provide start date.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Please provide end time.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please provide start time.", Toast.LENGTH_SHORT).show()
            }
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
                        startTime_n = "$hour2:0$selectedMinute ${AM_PM}"
                    }else{
                        startTimeTv.setText("$hour2:$selectedMinute ${AM_PM}")
                        startTime_n = "$hour2:$selectedMinute ${AM_PM}"
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
                        endTime_n = "$hour2:0$selectedMinute ${AM_PM}"
                    }else{
                        endTimeTv.setText("$hour2:$selectedMinute ${AM_PM}")
                        endTime_n = "$hour2:$selectedMinute ${AM_PM}"
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

    fun getDate(picker: DatePicker): String? {
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
    fun getDate2(picker: DatePicker): String? {
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

        builder.append(day.toString() + "-")
        builder.append(month.toString() + "-") //month is 0 based
        builder.append(picker.getYear())

        return builder.toString()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    private fun getDurationHour(startDateTime: String, endDateTime: String): Long {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var durationTotalMin = 0

        try {
            val d1: Date = sdf.parse(startDateTime)
            val d2: Date = sdf.parse(endDateTime)

            val difference_In_Time = d2.time - d1.time

            val difference_In_Seconds = (difference_In_Time / 1000)% 60

            val difference_In_Minutes = (difference_In_Time / (1000 * 60))% 60

            val difference_In_Hours = (difference_In_Time / (1000 * 60 * 60))% 24

            val difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24 * 365))

            var difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24))% 365

            val durationDay = difference_In_Days.toInt()
            val durationHour = difference_In_Hours.toInt()

            durationTotalMin = (durationHour*60)+difference_In_Minutes.toInt()
        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }

        //return durationTotalMin.toLong()*60*1000
        return durationTotalMin.toLong()

    }

    private fun showAddressBottomSheet(
        subLocality: String,
        streetName: String = "",
        streetNumber: String = "",
        city: String? = null,
        state: String? = null,
        zipcode: String? = null,
        building: String? = null,
        floor: String? = null
    ){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.address_fill_bottomsheet_layout, null)

        val btnSave = view.findViewById<CardView>(R.id.save_address_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val streetTxt = view.findViewById<EditText>(R.id.street_txt)
        val cityTxt = view.findViewById<EditText>(R.id.city_txt)
        val stateTxt = view.findViewById<EditText>(R.id.state_txt)
        val zipcodeTxt = view.findViewById<EditText>(R.id.zipcode_txt)
        val buildingTxt = view.findViewById<EditText>(R.id.building_txt)
        val floorTxt = view.findViewById<EditText>(R.id.floor_txt)

        var streetVar = ""
        if(streetName.isEmpty() && streetNumber.isEmpty()){
            streetVar = ""
        }else if(streetName.isEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber
        }else if(streetName.isNotEmpty() && streetNumber.isEmpty()){
            streetVar = streetName
        }else if(streetName.isNotEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber+", "+streetName
        }

        streetVar?.let {
            streetTxt.text = Editable.Factory.getInstance().newEditable(streetVar)
        }

        city?.let{
            cityTxt.text = Editable.Factory.getInstance().newEditable(city)
        }
        state?.let {
            stateTxt.text = Editable.Factory.getInstance().newEditable(state)
        }
        zipcode?.let {
            zipcodeTxt.text = Editable.Factory.getInstance().newEditable(zipcode)
        }
        building?.let {
            buildingTxt.text = Editable.Factory.getInstance().newEditable(building)
        }
        floor?.let {
            floorTxt.text = Editable.Factory.getInstance().newEditable(floor)
        }

        btnClear.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            street_n = streetTxt.text.toString()
            city_n = cityTxt.text.toString()
            state_n = stateTxt.text.toString()
            zipcode_n = zipcodeTxt.text.toString()
            building_n = buildingTxt.text.toString()
            floor_n = floorTxt.text.toString()
            if(!street_n.isEmpty()){
                if(!city_n.isEmpty()){
                    if(!state_n.isEmpty()){
                        if(!zipcode_n.isEmpty()){
                            if(zipcode_n.length >= 5){
                                binding.addressCard.visible()
                                binding.showAddressCard.visible()

                                binding.fullAddressTv.text = subLocality+", "+street_n+", "+city_n+", "+state_n+", "+zipcode
                                binding.cityNameTv.text = city_n
                                binding.streetTv.text = street_n
                                binding.buildingTv.text = building_n

                                binding.showFullAddressTv.text = subLocality+", "+street_n+", "+city_n+", "+state_n+", "+zipcode
                                binding.showCityNameTv.text = city_n
                                binding.showStreetTv.text = street_n
                                binding.showBuildingTv.text = building_n
                                if(floor_n?.isNotEmpty() == true){
                                    binding.buildingTv.text = building_n+", "+floor_n
                                    binding.showBuildingTv.text = building_n+", "+floor_n
                                }
                                dialog.dismiss()
                            }else{
                                Toast.makeText(this,"provide a valid zipcode", Toast.LENGTH_SHORT).show()
                                zipcodeTxt.showKeyboard()
                            }
                        }else{
                            Toast.makeText(this,"provide zipcode", Toast.LENGTH_SHORT).show()
                            zipcodeTxt.showKeyboard()
                        }
                    }else{
                        Toast.makeText(this,"provide state name", Toast.LENGTH_SHORT).show()
                        stateTxt.showKeyboard()
                    }
                }else{
                    Toast.makeText(this,"provide city name", Toast.LENGTH_SHORT).show()
                    cityTxt.showKeyboard()
                }
            }else{
                Toast.makeText(this,"provide street name", Toast.LENGTH_SHORT).show()
                streetTxt.showKeyboard()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun jobPostObserve(){
        mPostJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        //Toast.makeText(this,outcome.data!!.message.toString(), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,PaymentDetailsActivity::class.java)
                        intent.putExtra("amount",outcome.data?.data!!.amount)
                        intent.putExtra("job_id",outcome.data?.data!!.id.toString())
                        intent.putExtra("from","activity")
                        startActivity(intent)
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
        binding.addPatient.gone()
        binding.addressCard.gone()

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

        binding.addPatient.setOnClickListener {
            showPatientDetailsBottomSheet()
        }

        binding.addressDeleteIcon.setOnClickListener {
            binding.cityNameTv.text = null
            binding.fullAddressTv.text = null
            binding.streetTv.text = null
            binding.buildingTv.text = null
            binding.addressCard.gone()
        }

        binding.requiredNextStepBtn.setOnClickListener {
            val job_title = binding.jobTitleTxt.text.toString()
            val amount = binding.addAmountTxt.text.toString()
            val jobLoc = binding.fullAddressTv.text.toString()
            val job_desc = binding.jobDescTxt.text.toString()

            if(!job_title.isEmpty()){
                if(!careType.isEmpty()){
                    if(!genderAgeList.isEmpty()){
                        if(!start_date.isEmpty() && !end_date.isEmpty()){
                            if(!startTime.isEmpty()){
                                if(!endTime.isEmpty()){
                                    if(!jobLoc.isEmpty()){
                                        if(!amount.isEmpty()){
                                            if(!street_n.isEmpty()){
                                                if(!job_desc.isEmpty()){
                                                    fillGenderAgeRecycler(genderAgeList, binding.showGenderAgeRecycler)
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
                                        Toast.makeText(this,"Please provide job location.",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this,"Please add patient details.",Toast.LENGTH_SHORT).show()
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
            //binding.showAddressTxt.text = Editable.Factory.getInstance().newEditable(job_address)

            binding.showJobDescTxt.text = Editable.Factory.getInstance().newEditable(binding.jobDescTxt.text.toString())
        }
    }

    private fun initializePageThree() {

        binding.showAddressDeleteIcon.setOnClickListener {
            binding.cityNameTv.text = null
            binding.fullAddressTv.text = null
            binding.streetTv.text = null
            binding.buildingTv.text = null
            street_n = ""

            binding.showAddressCard.gone()
        }

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
            /*val intent = Intent(this, SelectCareTypeActivity::class.java)
            startActivity(intent)*/
            showCareTypeBottomSheet()

        }

        binding.showAddPatient.setOnClickListener {
            showPatientDetailsBottomSheet()
        }

        binding.prevNextStepBtn.setOnClickListener {

            if(!genderAgeList.isEmpty()){
                if(!street_n.isEmpty()){
                    if(isConnectedToInternet()){
                        mPostJobViewModel.jobPost(
                            binding.showJobTitleTxt.text.toString(),
                            binding.showAddCareTypeHtv.text.toString(),
                            genderAgeList,
                            start_date,
                            end_date,
                            startTime,
                            endTime,
                            binding.showAmountTxt.text.toString(),
                            binding.showFullAddressTv.text.toString(),
                            binding.showJobDescTxt.text.toString(),
                            medicalHistoryList,
                            jobSkillList,
                            otherReqList,
                            checkList,
                            place_name,
                            lat,
                            lang,
                            street_n,
                            city_n,
                            state_n,
                            zipcode_n,
                            building_n,
                            floor_n,
                            "USA",
                            accessToken
                        )
                        loader.show()
                    }else{
                        Toast.makeText(this,"No internet connection.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Please provide address.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please select a care type.", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroy() {
        genderAgeList = mutableListOf()
        super.onDestroy()
    }
}