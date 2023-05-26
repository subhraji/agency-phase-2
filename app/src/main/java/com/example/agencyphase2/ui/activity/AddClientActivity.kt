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
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityAddClientBinding
import com.example.agencyphase2.databinding.ActivityAskLocationBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.fragment.ImagePreviewFragment
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.UploadDocListener
import com.example.agencyphase2.viewmodel.CreateClientViewModel
import com.example.agencyphase2.viewmodel.SignUpViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
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
class AddClientActivity : AppCompatActivity(), UploadDocListener {
    private lateinit var binding: ActivityAddClientBinding

    private val mCreateClientViewModel: CreateClientViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog
    private lateinit var accessToken: String
    val genderList: Array<String> =  arrayOf("Select gender", "Male", "Female", "Other")
    private lateinit var gender: String

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
    var country_n: String? = null

    private var imageUri: Uri? = null
    private var absolutePath: String? = null
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()
        loader = this.loadingDialog(false)

        //observe
        createClientObserve()

        //spinner
        setUpGenderSpinner()

        Places.initialize(applicationContext, getString(R.string.api_key))
        autocomplete()

        binding.addressCard.gone()

        binding.addressDeleteIcon.setOnClickListener {
            binding.cityNameTv.text = null
            binding.fullAddressTv.text = null
            binding.streetTv.text = null
            binding.buildingTv.text = null
            binding.addressCard.gone()
            street_n = ""
            city_n = ""
            state_n = ""
            zipcode_n = ""
            building_n = ""
            floor_n = ""
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.addImageBtn.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                dispatchGalleryIntent()
            }else{
                requestStoragePermission()
            }
        }

        binding.addBtn.setOnClickListener {
            val name = binding.fullNameTxt.text.toString()
            val email = binding.emailNameTxt.text.toString()
            val phone = binding.mobileNameTxt.text.toString()
            val age = binding.ageTxt.text.toString()

            if(!name.isEmpty()){
                if(!phone.isEmpty()){
                    if(!email.isEmpty()){
                        if(!age.isEmpty()){
                            if(!gender.isEmpty()){
                                if(!job_address.isEmpty()){
                                    if(absolutePath != null){
                                        if(isConnectedToInternet()){
                                            try {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    withContext(Dispatchers.Main) {
                                                        val file = File(absolutePath)
                                                        val compressedImageFile = Compressor.compress(this@AddClientActivity, file)
                                                        val imagePart = createMultiPart("photo", compressedImageFile)
                                                        if(isConnectedToInternet()){
                                                            mCreateClientViewModel.createClient(
                                                                photo = imagePart,
                                                                email = binding.emailNameTxt.text.toString(),
                                                                name = binding.fullNameTxt.text.toString(),
                                                                phone = binding.mobileNameTxt.text.toString(),
                                                                address = job_address,
                                                                short_address = job_address,
                                                                street = street_n,
                                                                appartment_or_unit = building_n,
                                                                floor_no = floor_n,
                                                                city = city_n,
                                                                zip_code = zipcode_n,
                                                                state = state_n,
                                                                country = "USA",
                                                                lat = lat,
                                                                long = lang,
                                                                age = age,
                                                                gender = gender,
                                                                token = accessToken
                                                            )
                                                            hideSoftKeyboard()
                                                            loader.show()
                                                        }else{
                                                            Toast.makeText(this@AddClientActivity,"No internet connection.", Toast.LENGTH_SHORT).show()
                                                        }

                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }else{
                                            Toast.makeText(this,"Oops!! No internet connection.", Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(this,"select profile image.", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"Provide address.", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this,"Please select a gender.", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this,"Please provide the age.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this,"Provide email address.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this,"Provide mobile number.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Provide the full name.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun setUpGenderSpinner(){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,genderList)
        binding.genderSpinner.adapter = arrayAdapter
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0){
                    gender = ""
                }else{
                    gender = genderList[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }
    }


    private fun autocomplete(){
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.client_autocomplete_fragment) as AutocompleteSupportFragment

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
                            if(zipcode_n.length >= 5){
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

    override fun uploadFile(path: String) {
        val uri = imageUri
        val imageStream: InputStream = uri?.let {
            contentResolver.openInputStream(
                it
            )
        }!!
        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
        binding.addImageBtn.setImageBitmap(selectedImage)
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

    private fun createClientObserve(){
        mCreateClientViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mCreateClientViewModel.navigationComplete()
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

}