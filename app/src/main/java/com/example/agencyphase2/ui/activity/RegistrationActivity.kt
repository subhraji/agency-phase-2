package com.example.agencyphase2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
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
import com.example.agencyphase2.utils.EditDeleteClickListener
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.UploadDocListener
import com.example.agencyphase2.viewmodel.*
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
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
class RegistrationActivity : AppCompatActivity(), UploadDocListener, EditDeleteClickListener {
    private lateinit var binding: ActivityRegistrationBinding
    val stateList: Array<String> =  arrayOf("State","AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY","DC")
    val legalStructureList: Array<String> =  arrayOf("Select legal structure", "Solo Proprietorship", "Partnership", "Corporation", "Limited Liability Company")
    val orgTypeList: Array<String> =  arrayOf("Select organisation type", "For profit", "Non profit")

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private var mImeiId: String? = null
    private var grantedOtherPermissions: Boolean = false
    private val PICK_IMAGE = 100

    var job_address: String = ""
    var place_name: String = ""
    var lat: String = ""
    var lang: String = ""

    var street_n = ""
    var city_n = ""
    var state_n = ""
    var zipcode_n = ""
    var building_n = ""
    var floor_n = ""

    private lateinit var accessToken: String
    //private val mEditBasicInfoViewModel: EditBasicInfoViewModel by viewModels()
    private val addBusinessInfoViewModel: AddBusinessInfoViewModel by viewModels()
    private val mAddOtherInfoViewModel: AddOtherInfoViewModel by viewModels()
    private val mGetAuthorizeOfficerViewModel: GetAuthorizeOfficerViewModel by viewModels()
    private val mDeleteAuthOfficerViewModel: DeleteAuthOfficerViewModel by viewModels()
    private val mChangeOwnerPhoneViewModel: ChangeOwnerPhoneViewModel by viewModels()
    private val mUpdateAuthOfficerStatusViewModel: UpdateAuthOfficerStatusViewModel by viewModels()

    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var dialog: Dialog

    var state: String = ""
    var number_employee: String = ""
    var legal_structure: String = ""
    var org_type: String = ""
    private var owner_mobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.relativeLay1.gone()
        binding.relativeLay2.gone()
        binding.relativeLay3.gone()
        binding.skipBtn.gone()
        binding.addressCard.gone()
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

        autocomplete()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.businessNextStepBtn.setOnClickListener {
            val validEmail = binding.companyEmailTxtLay.helperText == null && binding.companyEmailTxt.text.toString().isNotEmpty()
            val validMobile = binding.mobileNumberTxtLay.helperText == null && binding.mobileNumberTxt.text.toString().isNotEmpty()
            val validTax = binding.taxNumberTxtLay.helperText == null && binding.taxNumberTxt.text.toString().isNotEmpty()

            if(imageUri != null){
                if(validEmail){
                    if(validMobile){
                        if(validTax){
                            try {
                                CoroutineScope(Dispatchers.IO).launch {
                                    withContext(Dispatchers.Main) {
                                        val file = File(absolutePath)
                                        val compressedImageFile = Compressor.compress(this@RegistrationActivity, file)
                                        val imagePart = createMultiPart("photo", compressedImageFile)
                                        if(isConnectedToInternet()){
                                            addBusinessInfoViewModel.addBusinessInfo(
                                                imagePart,
                                                binding.mobileNumberTxt.text.toString(),
                                                binding.companyEmailTxt.text.toString(),
                                                binding.taxNumberTxt.text.toString(),
                                                street_n,
                                                city_n,
                                                state_n,
                                                zipcode_n,
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
                        }else{
                            if(binding.taxNumberTxtLay.helperText == null) binding.taxNumberTxtLay.helperText = "required"
                            Toast.makeText(this,binding.taxNumberTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                            binding.taxNumberTxt.showKeyboard()
                        }
                    }else{
                        if(binding.mobileNumberTxtLay.helperText == null) binding.mobileNumberTxtLay.helperText = "required"
                        Toast.makeText(this,binding.mobileNumberTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                        binding.mobileNumberTxt.showKeyboard()
                    }
                }else{
                    if(binding.companyEmailTxtLay.helperText == null) binding.companyEmailTxtLay.helperText = "required"
                    Toast.makeText(this,binding.companyEmailTxtLay.helperText.toString(),Toast.LENGTH_SHORT).show()
                    binding.companyEmailTxt.showKeyboard()
                }
            }else{
                Toast.makeText(this,"Please select a profile picture.",Toast.LENGTH_SHORT).show()
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
            if(!owner_mobile.isEmpty()){
                mUpdateAuthOfficerStatusViewModel.updateAuthOfficerStatus(accessToken)
                loader.show()
            }else{
                showMobileDialog()
                Toast.makeText(this,"Please add your mobile number",Toast.LENGTH_SHORT).show()
            }
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
        setUpLsSpinner()
        setUpOrgTypeSpinner()

        //observe
        addBusinessObserve()
        addOtherInfoObserve()
        getAuthorizeInoObserve()
        deleteAuthOfficerObserver()
        updateAuthOfficerStatusObserver()

        //listener
        emailFocusListener()
        mobileFocusListener()
        taxFocusListener()
    }

    override fun onResume() {
        if(isConnectedToInternet()){
            binding.getAuthOfficerShimmerView.visible()
            binding.getAuthOfficerShimmerView.startShimmer()
            binding.authOfficerRecycler.gone()
            mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
        }else{
            Toast.makeText(this,"No internet connection.", Toast.LENGTH_LONG).show()
        }
        super.onResume()
    }

    private fun autocomplete(){
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        val etTextInput: EditText = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etTextInput.setTextColor(R.color.black)
        etTextInput.setTextSize(14.5f)
        etTextInput.setHint(R.string.search_loc)
        etTextInput.setHintTextColor(R.color.black)

        val ivSearch: ImageView = findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button)
        ivSearch.setImageResource(R.drawable.ic_gps_19)

        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
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
            streetVar = " "
        }else if(streetName.isEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber
        }else if(streetName.isNotEmpty() && streetNumber.isEmpty()){
            streetVar = streetName
        }else if(streetName.isNotEmpty() && streetNumber.isNotEmpty()){
            streetVar = streetNumber+", "+streetName
        }

        streetTxt.text = Editable.Factory.getInstance().newEditable(streetVar)

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
                            if(zipcode_n.length == 5){
                                if(!building_n.isEmpty()){
                                    binding.addressCard.visible()

                                    binding.fullAddressTv.text = subLocality+", "+street_n+", "+city_n+", "+state_n+", "+zipcode
                                    binding.cityNameTv.text = city_n
                                    binding.streetTv.text = street_n
                                    binding.buildingTv.text = building_n

                                    if(!floor_n.isEmpty()){
                                        binding.buildingTv.text = building_n+", "+floor_n
                                    }
                                    dialog.dismiss()
                                }else{
                                    Toast.makeText(this,"provide building name or number", Toast.LENGTH_SHORT).show()
                                    buildingTxt.showKeyboard()
                                }
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


    private fun emailFocusListener(){
        binding.companyEmailTxt.doOnTextChanged { text, start, before, count ->
            binding.companyEmailTxtLay.helperText = validEmail()
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.companyEmailTxt.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }

        return null
    }

    private fun mobileFocusListener(){
        binding.mobileNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.mobileNumberTxtLay.helperText = validMobileNumber()
        }
    }

    private fun validMobileNumber(): String? {
        val mobileText = binding.mobileNumberTxt.text.toString()
        if(mobileText.length != 10){
            return "10 digit number required."
        }

        if(binding.mobileNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid phone number."
        }

        return  null
    }

    private fun taxFocusListener(){
        binding.taxNumberTxt.doOnTextChanged { text, start, before, count ->
            binding.taxNumberTxtLay.helperText = validTaxNumber()
        }
    }

    private fun validTaxNumber(): String? {
        val mobileText = binding.taxNumberTxt.text.toString()
        if(mobileText.length != 9){
            return "9 digit number required."
        }

        if(binding.taxNumberTxt.text.toString().toDouble() == 0.00){
            return "Please provide a valid tax number."
        }

        return  null
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
                        binding.titleTv.text = "Other Information"
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
                        binding.titleTv.text = "Authorized Officer"
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
                    binding.getAuthOfficerShimmerView.gone()
                    binding.getAuthOfficerShimmerView.stopShimmer()
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null && outcome.data?.data?.size != 0){
                            binding.authOfficerRecycler.visible()
                            val list = outcome.data?.data?.filterIndexed { index, data -> index!=0 }
                            list?.let {
                                fillAuthRecyclerView(it)
                            }

                            binding.fullNameTv.text = outcome.data?.data!![0].name
                            binding.emailTv.text = outcome.data?.data!![0].email
                            binding.roleTv.text = outcome.data?.data!![0].role

                            if(outcome.data?.data!![0].phone != null){
                                binding.addMobileBtn.gone()
                                binding.mobileTv.text = outcome.data?.data!![0].phone.toString()
                                owner_mobile = outcome.data?.data!![0].phone.toString()
                            }else{
                                owner_mobile = ""
                                binding.mobileTv.text = ""
                                binding.addMobileBtn.visible()
                                binding.addMobileBtn.setOnClickListener {
                                    showMobileDialog()
                                }
                            }
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

    private fun deleteAuthOfficerObserver(){
        mDeleteAuthOfficerViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        mDeleteAuthOfficerViewModel.navigationComplete()
                        binding.getAuthOfficerShimmerView.visible()
                        binding.getAuthOfficerShimmerView.startShimmer()
                        binding.authOfficerRecycler.gone()
                        mGetAuthorizeOfficerViewModel.getAuthOfficer(accessToken)
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
            adapter = AuthorizeOfficerAdapter(list,this@RegistrationActivity, this@RegistrationActivity)
        }
    }

    private fun showMobileDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.add_mobile_number_layout)
        val mobile_txt = dialog.findViewById<TextInputEditText>(R.id.mobile_number_txt)
        val submit = dialog.findViewById<TextView>(R.id.submit_btn)
        val cancel = dialog.findViewById<TextView>(R.id.cancel_btn)

        submit.setOnClickListener {
            val mobile_number = mobile_txt.text.toString()
            if(!mobile_number.isEmpty()){
                //mEditBasicInfoViewModel.editBasicInfo(phone = mobile_number, token = accessToken)
                mChangeOwnerPhoneViewModel.changeOwnerPhone(phone = mobile_number, token = accessToken)
                loader.show()

                addMobileNumberObserver(mobile_number)
                //dialog.dismiss()
            }else{
                owner_mobile = ""
                Toast.makeText(this,"Please enter your mobile number",Toast.LENGTH_SHORT).show()
                mobile_txt.showKeyboard()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addMobileNumberObserver(mobile: String?){
        mChangeOwnerPhoneViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        binding.addMobileBtn.gone()
                        binding.mobileTv.text = mobile
                        owner_mobile = mobile!!
                        dialog.dismiss()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                    mChangeOwnerPhoneViewModel.navigationComplete()
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

    private fun updateAuthOfficerStatusObserver(){
        mUpdateAuthOfficerStatusViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                    }
                    mUpdateAuthOfficerStatusViewModel.navigationComplete()
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

    override fun onClick(view: View, id: Int) {
        showRemoveAuthOfficerDialog(id)
    }

    private fun showRemoveAuthOfficerDialog(id: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remove Authorize Officer")
        builder.setMessage("Do you want to remove the authorize officer ?")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            if(isConnectedToInternet()){
                mDeleteAuthOfficerViewModel.deleteAuthOfficer(accessToken,id)
                loader.show()
            }else{
                Toast.makeText(this,"No internet connection.",Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}