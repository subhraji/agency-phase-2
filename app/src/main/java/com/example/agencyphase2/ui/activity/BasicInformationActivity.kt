package com.example.agencyphase2.ui.activity

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.AddBusinessInfoViewModel
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.user.caregiver.gone
import com.user.caregiver.loadingDialog
import com.user.caregiver.showKeyboard
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasicInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBasicInformationBinding
    val legalStructureList: Array<String> =  arrayOf("Select legal structure", "Solo Proprietorship", "Partnership", "Corporation", "Limited Liability Company")
    val orgTypeList: Array<String> =  arrayOf("Select organisation type", "For profit", "Non profit")
    val stateList: Array<String> =  arrayOf("State","AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY","DC")
    private var legal_structure: String = ""
    private var org_type: String = ""
    private var state: String = ""
    private lateinit var accessToken: String
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private val addBusinessInfoViewModel: AddBusinessInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBasicInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()

        binding.backArrow.setOnClickListener {
            finish()
        }

        //spinner
        setUpLsSpinner()
        setUpOrgTypeSpinner()
        setUpStateSpinner()

        //validation
        mobileNumberFocusListener()
        taxIdFocusListener()
        streetFocusListener()
        cityFocusListener()
        zipCodeFocusListener()

        //observer
        addBusinessObserve()

        binding.nextStepBtn.setOnClickListener {
            val validMobileNumber = binding.mobileNumberTxtLay.helperText == null
            val validTaxIdNumber = binding.taxNumberTxtLay.helperText == null
            val validStreetNumber = binding.streetNameTxtLay.helperText == null
            val validCity = binding.cityTxtLay.helperText == null
            val validZipcode = binding.zipcodeTxtLay.helperText == null

            if(validMobileNumber){
                if(legal_structure != ""){
                    binding.lsRequired.gone()
                    if(org_type != ""){
                        binding.orgTypeRequired.gone()
                        if(validTaxIdNumber){
                            if(validStreetNumber){
                                if(validCity){
                                    if(state != ""){
                                        binding.stateRequired.gone()
                                        if(validZipcode){

                                            /*addBusinessInfoViewModel.addBusinessInfo(
                                                binding.mobileNumberTxt.text.toString(),
                                                legal_structure,
                                                org_type,
                                                binding.taxNumberTxt.text.toString(),
                                                binding.streetNameTxt.text.toString(),
                                                binding.cityTxt.text.toString(),
                                                state,
                                                binding.zipcodeTxt.text.toString(),
                                                token = accessToken
                                            )

                                            loader = this.loadingDialog()
                                            loader.show()*/

                                        }else{
                                            Toast.makeText(this,binding.zipcodeTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                                            binding.zipcodeTxt.showKeyboard()
                                        }
                                    }else{
                                        binding.stateRequired.visible()
                                        binding.stateRequired.text = "Select state"
                                        Toast.makeText(this,"Select state", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,binding.cityTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                                    binding.cityTxt.showKeyboard()
                                }
                            }else{
                                Toast.makeText(this,binding.streetNameTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                                binding.streetNameTxt.showKeyboard()
                            }
                        }else{
                            Toast.makeText(this,binding.taxNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                            binding.taxNumberTxt.showKeyboard()
                        }
                    }else{
                        binding.orgTypeRequired.visible()
                        binding.orgTypeRequired.text = "select organisation type"
                        Toast.makeText(this,"select organisation type", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    binding.lsRequired.visible()
                    binding.lsRequired.text = "select legal structure"
                    Toast.makeText(this,"select legal structure", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,binding.mobileNumberTxtLay.helperText.toString(), Toast.LENGTH_SHORT).show()
                binding.mobileNumberTxt.showKeyboard()
            }

        }
    }

    private fun setUpLsSpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,legalStructureList)
        binding.lsSpinner.adapter = arrayAdapter
        binding.lsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    binding.lsRequired.visible()
                    legal_structure = ""
                }else{
                    binding.lsRequired.gone()
                    legal_structure = legalStructureList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }

    private fun setUpOrgTypeSpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,orgTypeList)
        binding.orgTypeSpinner.adapter = arrayAdapter
        binding.orgTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    binding.orgTypeRequired.visible()
                    org_type = ""
                }else{
                    binding.orgTypeRequired.gone()
                    org_type = orgTypeList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    private fun setUpStateSpinner(){
        val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item,stateList)
        binding.stateSpinner.adapter = arrayAdapter
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    binding.stateRequired.visible()
                    state = ""
                }else{
                    binding.stateRequired.gone()
                    state = stateList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    private fun mobileNumberFocusListener(){
        binding.mobileNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.mobileNumberTxtLay.helperText = validMobileNumber()
        }
    }

    private fun validMobileNumber(): String? {
        val numberText = binding.mobileNumberTxt.text.toString()

        if(numberText.isEmpty()){
            return "Provide mobile number."
        }
        if(numberText.length != 10){
            return "Mobile number must be a 10 digit format."
        }
        if(numberText.toDouble() == 0.00){
            return "Invalid mobile number."
        }
        return null
    }

    private fun taxIdFocusListener(){
        binding.taxNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.taxNumberTxtLay.helperText = validTaxIdNumber()
        }
    }

    private fun validTaxIdNumber(): String? {
        val taxText = binding.taxNumberTxt.text.toString()

        if(taxText.isEmpty()){
            return "Provide tax id/ EIN number."
        }
        if(taxText.length != 9){
            return "Tax id or EIN number must be a 9 digit format."
        }
        if(taxText.toDouble() == 0.00){
            return "Invalid tax id/ EIN number."
        }
        return null
    }

    private fun streetFocusListener(){
        binding.streetNameTxt.doOnTextChanged { text, start, before, count ->
            binding.streetNameTxtLay.helperText = validStreet()
        }
    }

    private fun validStreet(): String? {
        val streetText = binding.streetNameTxt.text.toString()

        if(streetText.isEmpty()){
            return "Provide street number, name"
        }
        return null
    }

    private fun cityFocusListener(){
        binding.cityTxt.doOnTextChanged { text, start, before, count ->
            binding.cityTxtLay.helperText = validCity()
        }
    }

    private fun validCity(): String? {
        val cityText = binding.cityTxt.text.toString()

        if(cityText.isEmpty()){
            return "Provide city area/District"
        }
        return null
    }

    private fun zipCodeFocusListener(){
        binding.zipcodeTxt.doOnTextChanged { text, start, before, count ->
            binding.zipcodeTxtLay.helperText = validZipcode()
        }
    }

    private fun validZipcode(): String? {
        val zipcodeText = binding.zipcodeTxt.text.toString()

        if(zipcodeText.isEmpty()){
            return "Provide zipcode"
        }
        if(zipcodeText.length != 5){
            return "Zipcode must be a 5 digit format"
        }
        if(zipcodeText.toDouble() == 0.00){
            return "Invalid zipcode"
        }
        return null
    }

    private fun addBusinessObserve(){
        addBusinessInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        /*val intent = Intent(this, OptionalBusinessInfoActivity::class.java)
                        startActivity(intent)*/
                        addBusinessInfoViewModel.navigationComplete()
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

}