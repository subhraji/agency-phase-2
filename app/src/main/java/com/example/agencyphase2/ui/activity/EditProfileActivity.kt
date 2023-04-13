package com.example.agencyphase2.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityCreateClientBinding
import com.example.agencyphase2.databinding.ActivityEditProfileBinding
import com.example.agencyphase2.model.pojo.get_profile.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.fragment.ImagePreviewFragment
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.UploadDocListener
import com.example.agencyphase2.viewmodel.EditBasicInfoViewModel
import com.example.agencyphase2.viewmodel.PostJobViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.user.caregiver.createMultiPart
import com.user.caregiver.hideSoftKeyboard
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity(), UploadDocListener {
    private lateinit var binding: ActivityEditProfileBinding

    private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    val legalStructureList: Array<String> =  arrayOf("Select legal structure", "Solo Proprietorship", "Partnership", "Corporation", "Limited Liability Company")
    val orgTypeList: Array<String> =  arrayOf("Select organisation type", "For profit", "Non profit")
    val employeeNoList: Array<String> =  arrayOf("Select number of employee", "0-10", "10-50", "50-100", "100+")
    var number_employee: String = ""
    var legal_structure: String = ""
    var org_type: String = ""

    private var mobileNumber: String? = null
    private var yearsBusiness: String? = null
    private var annualRevenue: String? = null
    private var country: String? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            mobileNumber = intent?.getStringExtra("mobileNumber")
            yearsBusiness = intent?.getStringExtra("yearsBusiness")
            annualRevenue = intent?.getStringExtra("annualRevenue")
            country = intent?.getStringExtra("country")
            image = intent?.getStringExtra("image")

            val noEmployee: String? = intent?.getStringExtra("noEmployee")
            val legalStructure: String? = intent?.getStringExtra("legalStructure")
            val orgType: String? = intent?.getStringExtra("orgType")

            //spinner
            setUpLsSpinner(legalStructure)
            setUpOrgTypeSpinner(orgType)
            setUpEmployeeNoSpinner(noEmployee)

            mobileNumber?.let {
                binding.mobileNumberTxt.text = Editable.Factory.getInstance().newEditable(mobileNumber)
            }
            yearsBusiness?.let {
                binding.yearsInBusinessTxt.text = Editable.Factory.getInstance().newEditable(yearsBusiness)
            }
            annualRevenue?.let {
                binding.revenueTxt.text = Editable.Factory.getInstance().newEditable(annualRevenue)
            }
            country?.let {
                binding.countryOfBusinessTxt.text = Editable.Factory.getInstance().newEditable(country)
            }

            Glide.with(this).load(Constants.PUBLIC_URL+ image)
                .placeholder(R.color.color_grey)
                .into(binding.userImg)
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        binding.backBtn.setOnClickListener {
            finish()
        }

        //observer
        editProfileObserve()

        binding.updateBtn.setOnClickListener {
            if(imageUri != null){
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            val file = File(absolutePath)
                            val compressedImageFile = Compressor.compress(this@EditProfileActivity, file)
                            val imagePart = createMultiPart("photo", compressedImageFile)
                            if(isConnectedToInternet()){
                                mEditBasicInfoViewModel.editBasicInfo(
                                    photo = imagePart,
                                    phone = binding.mobileNumberTxt.text.toString(),
                                    legal_structure = legal_structure,
                                    organization_type = org_type,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    number_of_employee = number_employee,
                                    years_in_business = binding.yearsInBusinessTxt.text.toString(),
                                    country_of_business = binding.countryOfBusinessTxt.text.toString(),
                                    annual_business_revenue = binding.revenueTxt.text.toString(),
                                    token = accessToken
                                )
                                hideSoftKeyboard()
                                loader.show()
                            }else{
                                Toast.makeText(this@EditProfileActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }else{
                if(isConnectedToInternet()){
                    mEditBasicInfoViewModel.editBasicInfo(
                        photo = null,
                        phone = binding.mobileNumberTxt.text.toString(),
                        legal_structure = legal_structure,
                        organization_type = org_type,
                        null,
                        null,
                        null,
                        null,
                        null,
                        number_of_employee = number_employee,
                        years_in_business = binding.yearsInBusinessTxt.text.toString(),
                        country_of_business = binding.countryOfBusinessTxt.text.toString(),
                        annual_business_revenue = binding.revenueTxt.text.toString(),
                        token = accessToken
                    )
                    hideSoftKeyboard()
                    loader.show()
                }else{
                    Toast.makeText(this@EditProfileActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addImageBtn.setOnClickListener {

            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchGalleryIntent()
            }else{
                //requestPermission()
                requestStoragePermission()
            }
        }
    }

    private fun dispatchGalleryIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = this.contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun showImageDialog(absolutePath: String,uri: String,type: String) {
        val bundle = Bundle()
        bundle.putString("path", absolutePath)
        bundle.putString("uri",uri)
        bundle.putString("type",type)
        val dialogFragment = ImagePreviewFragment(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(this.supportFragmentManager, "signature")
    }

    private fun requestStoragePermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    dispatchGalleryIntent()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requestStoragePermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            })
            .onSameThread()
            .check()
    }

    private fun setUpOrgTypeSpinner(value: String? = null){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,orgTypeList)
        binding.orgTypeSpinner.adapter = arrayAdapter

        if (value != null) {
            val spinnerPosition: Int = arrayAdapter.getPosition(value)
            binding.orgTypeSpinner.setSelection(spinnerPosition)
        }

        binding.orgTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    org_type = ""
                }else{
                    org_type = orgTypeList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }

    private fun setUpLsSpinner(value: String? = null){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,legalStructureList)
        binding.lsSpinner.adapter = arrayAdapter

        if (value != null) {
            val spinnerPosition: Int = arrayAdapter.getPosition(value)
            binding.lsSpinner.setSelection(spinnerPosition)
        }

        binding.lsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    legal_structure = ""
                }else{
                    legal_structure = legalStructureList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }

    private fun setUpEmployeeNoSpinner(value: String? = null){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,employeeNoList)
        binding.numberEmployeeSpinner.adapter = arrayAdapter

        if (value != null) {
            val spinnerPosition: Int = arrayAdapter.getPosition(value)
            binding.numberEmployeeSpinner.setSelection(spinnerPosition)
        }

        binding.numberEmployeeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    number_employee = ""
                }else{
                    number_employee = employeeNoList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }

    private fun editProfileObserve(){
        mEditBasicInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        mEditBasicInfoViewModel.navigationComplete()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                imageUri = data?.data
                val path = getRealPathFromUri(imageUri)
                val imageFile = File(path!!)
                absolutePath = imageFile.absolutePath
                showImageDialog(imageFile.absolutePath,imageUri.toString(),"covid")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun uploadFile(path: String) {
        val uri = imageUri
        val imageStream: InputStream = uri?.let {
            this.contentResolver.openInputStream(
                it
            )
        }!!
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        binding.userImg.setImageBitmap(selectedImage)
    }

}