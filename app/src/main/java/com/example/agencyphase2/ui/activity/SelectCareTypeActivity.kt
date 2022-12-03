package com.example.agencyphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivitySelectCareTypeBinding
import com.example.agencyphase2.model.pojo.GenderAgeItemCountModel
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.EditBasicInfoViewModel
import com.user.caregiver.gone
import com.user.caregiver.visible

class SelectCareTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectCareTypeBinding
    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    private var numberOfPerson: Int? = null
    private var careType: String = ""

    //gender age
    private var gender1: String? = null
    private var gender2: String? = null
    private var gender3: String? = null
    private var gender4: String? = null
    private var gender5: String? = null

    private var age1: String? = null
    private var age2: String? = null
    private var age3: String? = null
    private var age4: String? = null
    private var age5: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySelectCareTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        if(JobPostActivity.genderAgeList.isNotEmpty()){

        }

        binding.personNoHtv.gone()
        binding.careTypeLay.gone()
        binding.genderAgeHtvLay.gone()
        binding.genderAge1.gone()
        binding.genderAge2.gone()
        binding.genderAge3.gone()
        binding.genderAge4.gone()
        binding.genderAge5.gone()
        JobPostActivity.genderAgeList = mutableListOf()

        setUpRadioButtons()

        binding.addBtn.setOnClickListener {
            val age1 = binding.ageTxt1.text.toString()
            val age2 = binding.ageTxt2.text.toString()
            val age3 = binding.ageTxt3.text.toString()
            val age4 = binding.ageTxt4.text.toString()
            val age5 = binding.ageTxt5.text.toString()
            if(numberOfPerson != null){
                if(numberOfPerson == 1){
                    if(gender1 != null){
                        if(age1.isNotEmpty()){
                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender1,age1))
                            finish()
                        }else{
                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                    }
                }else if(numberOfPerson == 2){
                    if(gender1 != null){
                        if(age1.isNotEmpty()){
                            if(gender2 != null){
                                if(age2.isNotEmpty()){
                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender1,age1))
                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender2,age2))
                                    finish()
                                }else{
                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                    }
                }else if(numberOfPerson == 3){
                    if(gender1 != null){
                        if(age1.isNotEmpty()){
                            if(gender2 != null){
                                if(age2.isNotEmpty()){
                                    if(gender3 != null){
                                        if(age3.isNotEmpty()){
                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender1,age1))
                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender2,age2))
                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender3,age3))
                                            finish()
                                        }else{
                                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                    }
                }else if(numberOfPerson == 4){
                    if(gender1 != null){
                        if(age1.isNotEmpty()){
                            if(gender2 != null){
                                if(age2.isNotEmpty()){
                                    if(gender3 != null){
                                        if(age3.isNotEmpty()){
                                            if(gender4 != null){
                                                if(age4.isNotEmpty()){
                                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender1,age1))
                                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender2,age2))
                                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender3,age3))
                                                    JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender4,age4))
                                                    finish()
                                                }else{
                                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                                }
                                            }else{
                                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                    }
                }else if(numberOfPerson == 5){
                    if(gender1 != null){
                        if(age1.isNotEmpty()){
                            if(gender2 != null){
                                if(age2.isNotEmpty()){
                                    if(gender3 != null){
                                        if(age3.isNotEmpty()){
                                            if(gender4 != null){
                                                if(age4.isNotEmpty()){
                                                    if(gender5 != null){
                                                        if(age5.isNotEmpty()){
                                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender1,age1))
                                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender2,age2))
                                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender3,age3))
                                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender4,age4))
                                                            JobPostActivity.genderAgeList.add(GenderAgeItemCountModel("senior",gender5,age5))
                                                            finish()
                                                        }else{
                                                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                                        }
                                                    }else{
                                                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                                    }
                                                }else{
                                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                                }
                                            }else{
                                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"please provide the age of patient.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"please select the gender.",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Please select number of person need care.",Toast.LENGTH_LONG).show()
            }
        }

        binding.tv1.setOnClickListener {
            backgroundGrey()
            binding.tv1.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv1.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 1
            binding.genderAgeHtvLay.visible()
            binding.genderAge1.visible()
            binding.genderAge2.gone()
            binding.genderAge3.gone()
            binding.genderAge4.gone()
            binding.genderAge5.gone()
        }
        binding.tv2.setOnClickListener {
            backgroundGrey()
            binding.tv2.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv2.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 2
            binding.genderAgeHtvLay.visible()
            binding.genderAge1.visible()
            binding.genderAge2.visible()
            binding.genderAge3.gone()
            binding.genderAge4.gone()
            binding.genderAge5.gone()
        }
        binding.tv3.setOnClickListener {
            backgroundGrey()
            binding.tv3.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv3.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 3
            binding.genderAgeHtvLay.visible()
            binding.genderAge1.visible()
            binding.genderAge2.visible()
            binding.genderAge3.visible()
            binding.genderAge4.gone()
            binding.genderAge5.gone()
        }
        binding.tv4.setOnClickListener {
            backgroundGrey()
            binding.tv4.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv4.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 4
            binding.genderAgeHtvLay.visible()
            binding.genderAge1.visible()
            binding.genderAge2.visible()
            binding.genderAge3.visible()
            binding.genderAge4.visible()
            binding.genderAge5.gone()
        }
        binding.tv5.setOnClickListener {
            backgroundGrey()
            binding.tv5.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv5.setTextColor(resources.getColor(R.color.white, null))
            numberOfPerson = 5
            binding.genderAgeHtvLay.visible()
            binding.genderAge1.visible()
            binding.genderAge2.visible()
            binding.genderAge3.visible()
            binding.genderAge4.visible()
            binding.genderAge5.visible()
        }

        //care type
        binding.seniorLay.setOnClickListener {
            binding.seniorLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.specialLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.specialImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.oldLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.oldImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.seniorTv.setTextColor(resources.getColor(R.color.white, null))
            binding.childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.specialTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.oldTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "senior care"
            binding.personNoHtv.visible()
            binding.careTypeLay.visible()
        }
        binding.childLay.setOnClickListener {
            binding.seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.childLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.childImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.specialLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.specialImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.oldLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.oldImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.childTv.setTextColor(resources.getColor(R.color.white, null))
            binding.patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.specialTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.oldTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "child care"
            binding.personNoHtv.visible()
            binding.careTypeLay.visible()
        }
        binding.patientLay.setOnClickListener {
            binding.seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.patientLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.patientImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.specialLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.specialImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.oldLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.oldImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.patientTv.setTextColor(resources.getColor(R.color.white, null))
            binding.specialTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.oldTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "patient care"
            binding.personNoHtv.visible()
            binding.careTypeLay.visible()
        }
        binding.specialLay.setOnClickListener {
            binding.seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.specialLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.specialImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.oldLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.oldImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.specialTv.setTextColor(resources.getColor(R.color.white, null))
            binding.oldTv.setTextColor(resources.getColor(R.color.text_grey, null))
            careType = "special care"
            binding.personNoHtv.visible()
            binding.careTypeLay.visible()
        }
        binding.oldLay.setOnClickListener {
            binding.seniorLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.seniorImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.childLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.childImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.patientLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.patientImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.specialLay.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.specialImg.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.oldLay.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.oldImg.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.seniorTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.childTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.patientTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.specialTv.setTextColor(resources.getColor(R.color.text_grey, null))
            binding.oldTv.setTextColor(resources.getColor(R.color.white, null))
            careType = "old care"
            binding.personNoHtv.visible()
            binding.careTypeLay.visible()
        }
    }

    private fun backgroundGrey(){
        binding.tv1.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv1.setTextColor(resources.getColor(R.color.black, null))
        binding.tv2.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv2.setTextColor(resources.getColor(R.color.black, null))
        binding.tv3.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv3.setTextColor(resources.getColor(R.color.black, null))
        binding.tv4.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv4.setTextColor(resources.getColor(R.color.black, null))
        binding.tv5.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
        binding.tv5.setTextColor(resources.getColor(R.color.black, null))
    }

    private fun setUpRadioButtons(){
        binding.typeOfCareRbg1.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb_1 -> {
                    gender1 = "male"
                }
                R.id.female_rb_1 -> {
                    gender1 = "female"
                }
                R.id.other_rb_1 -> {
                    gender1 = "other"
                }
            }
        })
        binding.typeOfCareRbg2.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb_2 -> {
                    gender2 = "male"
                }
                R.id.female_rb_2 -> {
                    gender2 = "female"
                }
                R.id.other_rb_2 -> {
                    gender2 = "others"
                }
            }
        })
        binding.typeOfCareRbg3.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb_3 -> {
                    gender3 = "male"
                }
                R.id.female_rb_3 -> {
                    gender3 = "female"
                }
                R.id.other_rb_3 -> {
                    gender3 = "other"
                }
            }
        })
        binding.typeOfCareRbg4.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb_4 -> {
                    gender4 = "male"
                }
                R.id.female_rb_4 -> {
                    gender4 = "female"
                }
                R.id.other_rb_4 -> {
                    gender4 = "other"
                }
            }
        })
        binding.typeOfCareRbg5.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male_rb_5 -> {
                    gender5 = "male"
                }
                R.id.female_rb_5 -> {
                    gender5 = "female"
                }
                R.id.other_rb_5 -> {
                    gender5 = "other"
                }
            }
        })
    }

}