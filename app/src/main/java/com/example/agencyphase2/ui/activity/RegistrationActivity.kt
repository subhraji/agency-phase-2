package com.example.agencyphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.AuthorizeOfficerAdapter
import com.example.agencyphase2.databinding.ActivityBasicInformationBinding
import com.example.agencyphase2.databinding.ActivityRegistrationBinding
import com.example.agencyphase2.model.pojo.get_authorize_officer.Data
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.fragment.ImagePreviewFragment
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.UploadDocListener
import com.example.agencyphase2.viewmodel.AddBusinessInfoViewModel
import com.example.agencyphase2.viewmodel.AddOtherInfoViewModel
import com.example.agencyphase2.viewmodel.GetAuthorizeOfficerViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.user.caregiver.*
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity(), UploadDocListener {
    private lateinit var binding: ActivityRegistrationBinding
    val stateList: Array<String> =  arrayOf("State","AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY","DC")
    val legalStructureList: Array<String> =  arrayOf("Select legal structure", "Solo Proprietorship", "Partnership", "Corporation", "Limited Liability Company")
    val orgTypeList: Array<String> =  arrayOf("Select organisation type", "For profit", "Non profit")

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    private lateinit var accessToken: String
    private val addBusinessInfoViewModel: AddBusinessInfoViewModel by viewModels()
    private val mAddOtherInfoViewModel: AddOtherInfoViewModel by viewModels()
    private val mGetAuthorizeOfficerViewModel: GetAuthorizeOfficerViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    var state: String = ""
    var number_employee: String = ""
    var legal_structure: String = ""
    var org_type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLay1.gone()
        binding.relativeLay2.gone()
        binding.relativeLay3.gone()
        binding.skipBtn.gone()
        binding.addressLinearLay.gone()
        loader = this@RegistrationActivity.loadingDialog()

        val extras = intent.extras
        if (extras != null) {
            val step = intent?.getIntExtra("step",0)
            if(step == 1){
                binding.relativeLay1.visible()
                binding.relativeLay2.gone()
                binding.relativeLay3.gone()
                binding.skipBtn.gone()
                binding.titleTv.text = "Business Information"
            }else if(step == 2){
                binding.relativeLay1.gone()
                binding.relativeLay2.visible()
                binding.relativeLay3.gone()
                binding.skipBtn.visible()
                binding.titleTv.text = "Other Information"
            }else if(step == 3){
                binding.relativeLay1.gone()
                binding.relativeLay2.gone()
                binding.relativeLay3.visible()
                binding.skipBtn.gone()
                binding.titleTv.text = "Authorized Officer"
            }else{
                binding.relativeLay1.gone()
                binding.relativeLay2.gone()
                binding.relativeLay3.visible()
                binding.skipBtn.gone()
                binding.titleTv.text = "Authorized Officer"
            }
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.businessNextStepBtn.setOnClickListener {
            val email = binding.companyEmailTxt.text.toString()
            val phone = binding.mobileNumberTxt.text.toString()
            val tax = binding.taxNumberTxt.text.toString()
            val street = binding.streetNameTxt.text.toString()
            val city = binding.cityTxt.text.toString()
            val zip_code = binding.zipcodeTxt.text.toString()

            try {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        val file = File(absolutePath)
                        val compressedImageFile = Compressor.compress(this@RegistrationActivity, file)
                        val imagePart = createMultiPart("photo", compressedImageFile)
                        if(isConnectedToInternet()){
                            addBusinessInfoViewModel.addBusinessInfo(
                                imagePart,
                                phone,
                                email,
                                tax,
                                street,
                                city,
                                state,
                                zip_code,
                                accessToken
                            )
                            hideSoftKeyboard()
                            loader.show()
                        }else{
                            Toast.makeText(this@RegistrationActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        binding.otherNextStepBtn.setOnClickListener {
            if(isConnectedToInternet()){
                mAddOtherInfoViewModel.addOtherInfo(
                    number_employee,
                    binding.revenueTxt.text.toString(),
                    binding.yearsInBusinessTxt.text.toString(),
                    legal_structure,
                    org_type,
                    binding.countryOfBusinessTxt.text.toString(),
                    accessToken
                )
                hideSoftKeyboard()
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show()
            }
        }

        binding.authOfficerNextStepBtn.setOnClickListener {
            finish()
        }

        binding.skipBtn.setOnClickListener {
            binding.relativeLay1.gone()
            binding.relativeLay2.gone()
            binding.relativeLay3.visible()
            binding.skipBtn.gone()
        }

        binding.imageAddBtn.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    dispatchGalleryIntent()
                }else{
                    requestPermission()
                }
            }else{
                requestPermission()
            }
        }

        binding.addressLay.setOnClickListener {
            binding.addressLinearLay.visible()
            binding.streetNameTxt.showKeyboard()
        }

        //other info number of employees
        binding.tv010.setOnClickListener {
            binding.tv010.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv010.setTextColor(resources.getColor(R.color.white, null))
            number_employee = "0-10"

            binding.tv050.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv050.setTextColor(resources.getColor(R.color.black, null))
            binding.tv50100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv50100.setTextColor(resources.getColor(R.color.black, null))
            binding.tv100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv100.setTextColor(resources.getColor(R.color.black, null))
        }
        binding.tv050.setOnClickListener {
            binding.tv050.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv050.setTextColor(resources.getColor(R.color.white, null))
            number_employee = "10-50"

            binding.tv010.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv010.setTextColor(resources.getColor(R.color.black, null))
            binding.tv50100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv50100.setTextColor(resources.getColor(R.color.black, null))
            binding.tv100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv100.setTextColor(resources.getColor(R.color.black, null))
        }
        binding.tv50100.setOnClickListener {
            binding.tv50100.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv50100.setTextColor(resources.getColor(R.color.white, null))
            number_employee = "50-100"

            binding.tv050.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv050.setTextColor(resources.getColor(R.color.black, null))
            binding.tv010.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv010.setTextColor(resources.getColor(R.color.black, null))
            binding.tv100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv100.setTextColor(resources.getColor(R.color.black, null))
        }
        binding.tv100.setOnClickListener {
            binding.tv100.background.setTint(ContextCompat.getColor(this, R.color.black))
            binding.tv100.setTextColor(resources.getColor(R.color.white, null))
            number_employee = "100+"

            binding.tv50100.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv50100.setTextColor(resources.getColor(R.color.black, null))
            binding.tv050.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv050.setTextColor(resources.getColor(R.color.black, null))
            binding.tv010.background.setTint(ContextCompat.getColor(this, R.color.color_grey))
            binding.tv010.setTextColor(resources.getColor(R.color.black, null))
        }

        binding.addOfficerBtn.setOnClickListener {
            val intent = Intent(this, AuthorizedPersonInfoAddActivity::class.java)
            startActivity(intent)
        }

        //spinner
        setUpStateSpinner()
        setUpLsSpinner()
        setUpOrgTypeSpinner()

        //observer
        addBusinessObserve()
        addOtherInfoObserve()
        getAuthorizeInoObserve()
    }

    override fun onResume() {
        if(isConnectedToInternet()){
            mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_LONG).show()
        }
        super.onResume()
    }

    private fun setUpOrgTypeSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,orgTypeList)
        binding.orgTypeSpinner.adapter = arrayAdapter
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

    private fun setUpLsSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,legalStructureList)
        binding.lsSpinner.adapter = arrayAdapter
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

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
            {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, 2296)
                } catch (e: java.lang.Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, 2296)
                }
            }else{
                dispatchGalleryIntent()
            }
        } else {
            requestStoragePermission()
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
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {

                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // info("onPermissionsChecked: All permissions are granted!")
                        val telephonyManager =
                            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        mImeiId =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                try {
                                    telephonyManager.imei
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                    "mxmxmxmxmxmxmxm"
                                }
                            } else {
                                "mxmxmxmxmxmxmxm"
                            }

                        grantedOtherPermissions = true
                    }

                    // check for permanent denial of any permission
                    /* if (report.isAnyPermissionPermanentlyDenied) {
                         // show alert dialog navigating to Settings
                         showSettingsDialog()
                     }*/
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
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

        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    dispatchGalleryIntent()
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private fun setUpStateSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,stateList)
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

    private fun addBusinessObserve(){
        addBusinessInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        binding.relativeLay1.gone()
                        binding.relativeLay3.gone()
                        binding.relativeLay2.visible()
                        binding.skipBtn.visible()
                        addBusinessInfoViewModel.navigationComplete()
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

    private fun addOtherInfoObserve(){
        mAddOtherInfoViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        binding.relativeLay1.gone()
                        binding.relativeLay2.gone()
                        binding.relativeLay3.visible()
                        binding.skipBtn.gone()
                        mAddOtherInfoViewModel.navigationComplete()
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

    private fun getAuthorizeInoObserve(){
        mGetAuthorizeOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            fillAuthRecyclerView(outcome.data?.data!!)
                        }else{

                        }
                        mGetAuthorizeOfficerViewModel.navigationComplete()
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

    private fun fillAuthRecyclerView(list: List<Data>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.authOfficerRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = AuthorizeOfficerAdapter(list,this@RegistrationActivity)
        }
    }

}