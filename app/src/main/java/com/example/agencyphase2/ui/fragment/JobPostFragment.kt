package com.example.agencyphase2.ui.fragment

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.MainActivity
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.ChipsAdapter
import com.example.agencyphase2.adapter.GenderAgeAdapter
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.databinding.FragmentJobPostBinding
import com.example.agencyphase2.databinding.FragmentPostBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.activity.JobPostActivity
import com.example.agencyphase2.ui.activity.PostJobPreviewActivity
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import com.example.agencyphase2.ui.activity.SelectCareTypeActivity
import com.example.agencyphase2.utils.PrefManager
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
import com.user.caregiver.gone
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class JobPostFragment : Fragment() {
    private var _binding: FragmentJobPostBinding? = null
    private val binding get() = _binding!!

    var careType:String = ""
    var gender:String = ""
    var age:String = ""
    var date:String = ""
    var startTime:String = ""
    var endTime:String = ""
    var job_address: String = ""
    var place_name: String = ""
    var lat: String = ""
    var lang: String = ""

    private val medicalHistoryList:MutableList<String> = mutableListOf()
    private val jobSkillList:MutableList<String> = mutableListOf()
    private val otherReqList:MutableList<String> = mutableListOf()
    private val checkList:MutableList<String> = mutableListOf()

    private lateinit var accessToken: String

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    //lists
    companion object {
        var genderAgeList: MutableList<GenderAgeItemCountModel> = mutableListOf()
    }

    //viewmodel
    private val mPostJobViewModel: PostJobViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        Places.initialize(context?.applicationContext, getString(R.string.api_key))

        //binding.relativeLay1.gone()
        binding.relativeLay2.gone()
        binding.relativeLay3.gone()
        binding.dateTimeLay.gone()
        loader = requireActivity().loadingDialog()

        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }

        //initialize page
        initializePageOne()
        initializePageTwo()
        initializePageThree()

        //observer
        jobPostObserve()
    }

    override fun onResume() {
        if(JobPostActivity.genderAgeList.isNotEmpty()){
            binding.typeOfCareLay.visible()
            binding.careTypeBtn.gone()
            careType = JobPostActivity.genderAgeList[0].careType.toString()
            binding.typeOfCareTv.text = careType
            binding.showCareTypeTv.text = careType
        }else{
            binding.typeOfCareLay.gone()
            binding.careTypeBtn.visible()
            careType = ""
        }
        fillGenderAgeRecycler(JobPostActivity.genderAgeList, binding.genderAgeRecycler)
        fillGenderAgeRecycler(JobPostActivity.genderAgeList, binding.showGenderAgeRecycler)
        super.onResume()
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
            .build(requireActivity())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun showDateTimeBottomSheet(){
        val dialog = BottomSheetDialog(requireActivity())
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
            mTimePicker = TimePickerDialog(requireActivity(),
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
            mTimePicker = TimePickerDialog(requireActivity(),
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
        }
        if(picker.getDayOfMonth() < 10){
            day = "0"+picker.getDayOfMonth().toString()
        }

        builder.append(month.toString() + "-") //month is 0 based
        builder.append(day.toString() + "-")
        builder.append(picker.getYear())

        return builder.toString()
    }

    private fun jobPostObserve(){
        mPostJobViewModel.response.observe(requireActivity(), Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(requireActivity(),outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mPostJobViewModel.navigationComplete()

                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
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

    private fun initializePageOne(){
        binding.dateTimeBtn.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.dateTimeLay.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.careTypeBtn.setOnClickListener {
            val intent = Intent(requireActivity(), SelectCareTypeActivity::class.java)
            startActivity(intent)
        }

        binding.typeOfCareLay.setOnClickListener {
            val intent = Intent(requireActivity(), SelectCareTypeActivity::class.java)
            startActivity(intent)
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
                                            Toast.makeText(requireActivity(),"Please provide the job description.",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(requireActivity(),"Please provide the address of this job.",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(requireActivity(),"Please type a remittance.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(requireActivity(),"Please select a end time for this job.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireActivity(),"Please select a start time for this job.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(requireActivity(),"Please select a date for this job.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireActivity(),"Please select a care type.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireActivity(),"Please provide the job title.",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(),"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(),"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(),"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(),"Please give your input on the text field.",Toast.LENGTH_SHORT).show()
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
        binding.prevNextStepBtn.setOnClickListener {
            mPostJobViewModel.jobPost(
                binding.showJobTitleTxt.text.toString(),
                binding.showCareTypeTv.text.toString(),
                JobPostActivity.genderAgeList,
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
        }

        fillMedicalRecycler(medicalHistoryList, binding.showMedicalHisRecycler)
        fillJobSkillRecycler(jobSkillList, binding.showJobSkillRecycler)
        fillOtherRequirementRecycler(otherReqList, binding.showOtherRequirementsRecycler)
        fillCheckListRecycler(checkList, binding.showCheckListRecycler)
    }

    private fun fillGenderAgeRecycler(list: List<GenderAgeItemCountModel>, recycleView: RecyclerView) {
        val gridLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recycleView.apply {
            layoutManager = gridLayoutManager
            adapter = GenderAgeAdapter(list,requireActivity(), careType)
        }
    }

    private fun fillMedicalRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(requireActivity())
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,requireActivity())
        }
    }

    private fun fillJobSkillRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(requireActivity())
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,requireActivity())
        }
    }

    private fun fillOtherRequirementRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(requireActivity())
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,requireActivity())
        }
    }

    private fun fillCheckListRecycler(list: MutableList<String>, recycleView: RecyclerView) {
        val linearlayoutManager = LinearLayoutManager(requireActivity())
        recycleView.apply {
            layoutManager = linearlayoutManager
            setHasFixedSize(true)
            isFocusable = false
            adapter = ChipsAdapter(list,requireActivity())
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

    override fun onDestroy() {
        JobPostActivity.genderAgeList = mutableListOf()
        super.onDestroy()
    }

}