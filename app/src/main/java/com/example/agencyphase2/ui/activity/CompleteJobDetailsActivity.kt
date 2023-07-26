package com.example.agencyphase2.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.BulletPointAdapter
import com.example.agencyphase2.databinding.ActivityCompleteJobDetailsBinding
import com.example.agencyphase2.databinding.ActivityJobPostBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.ui.fragment.CaregiverProfileFragment
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.AddReviewViewModel
import com.example.agencyphase2.viewmodel.CloseJobViewModel
import com.example.agencyphase2.viewmodel.GetCompleteJobViewModel
import com.example.agencyphase2.viewmodel.GetUpcommingJobViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.ncorti.slidetoact.SlideToActView
import com.user.caregiver.gone
import com.user.caregiver.isConnectedToInternet
import com.user.caregiver.loadingDialog
import com.user.caregiver.visible
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
class CompleteJobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteJobDetailsBinding

    private lateinit var accessToken: String
    private val mGetCompleteJobViewModel: GetCompleteJobViewModel by viewModels()
    private val mCloseJobViewModel: CloseJobViewModel by viewModels()
    private val mAddReviewViewModel: AddReviewViewModel by viewModels()
    private lateinit var loader: androidx.appcompat.app.AlertDialog

    private var id: Int = 0
    private var pageNumber = 1
    private var user_id: String? = null
    private var caregiver_name: String? = null
    private var caregiver_photo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCompleteJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+ PrefManager.getKeyAuthToken()
        loader = this.loadingDialog()

        val extras = intent.extras
        if (extras != null) {
            id = intent?.getIntExtra("id",0)!!
        }

        binding.medicalRecycler.gone()
        binding.medicalHisHtv.gone()
        binding.jobExpRecycler.gone()
        binding.jobExpHtv.gone()
        binding.otherReqRecycler.gone()
        binding.otherReqHtv.gone()
        binding.noCheckListTv.gone()
        binding.checkListRecycler.gone()

        clickJobOverview()
        addReviewObserver()
        closeJobObserver()

        binding.jobOverviewCard.setOnClickListener {
            clickJobOverview()
        }

        binding.checklistCard.setOnClickListener {
            clickCheckList()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.chatCard.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("caregiver_id",user_id.toString())
            intent.putExtra("name",caregiver_name.toString())
            intent.putExtra("photo",caregiver_photo.toString())
            intent.putExtra("job_id",id.toString())
            intent.putExtra("status","completed")
            startActivity(intent)
        }

        binding.profileCard.setOnClickListener {
            /*val intent = Intent(this,CareGiverProfileActivity::class.java)
            intent.putExtra("job_id", id.toString())
            startActivity(intent)*/
            val addPhotoBottomDialogFragment: CaregiverProfileFragment =
                CaregiverProfileFragment.newInstance()
            val bundle = Bundle()
            bundle.putString("id", id.toString())
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(
                supportFragmentManager,
                "caregiver_profile_fragment"
            )
        }

        binding.shimmerView.visible()
        binding.shimmerView.startShimmer()
        binding.mainLay.gone()
        if(isConnectedToInternet()){
            mGetCompleteJobViewModel.getCompleteJob(token = accessToken, pageNumber,id = id)
        }else{
            Snackbar.make(binding.root,"Oops!! No internet connection", Snackbar.LENGTH_SHORT).show()
        }


        binding.slideToCompleteBtn.onSlideToActAnimationEventListener = (object : SlideToActView.OnSlideToActAnimationEventListener{
            override fun onSlideCompleteAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@CompleteJobDetailsActivity,"onSlideCompleteAnimationEnded",Toast.LENGTH_SHORT).show()
                if(isConnectedToInternet()){
                    mCloseJobViewModel.closeJob(
                        id.toString(),
                        accessToken
                    )
                    loader.show()
                }else{
                    Toast.makeText(this@CompleteJobDetailsActivity,"Oops!!, No internet connection.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {
                //Toast.makeText(this@CompleteJobDetailsActivity,"onSlideCompleteAnimationStarted",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideResetAnimationEnded(view: SlideToActView) {
                //Toast.makeText(this@CompleteJobDetailsActivity,"onSlideResetAnimationEnded",Toast.LENGTH_SHORT).show()
            }

            override fun onSlideResetAnimationStarted(view: SlideToActView) {
                //showReviewDialog()
            }

        })

        binding.jobActivities.setOnClickListener {
            val intent = Intent(this,JobActivitiesActivity::class.java)
            startActivity(intent)
        }

        //observer
        getCompletedJobsObserve()
    }

    private fun showReviewDialog(){
        val dialog = BottomSheetDialog(this, R.style.DialogStyle)
        val view = layoutInflater.inflate(R.layout.add_review_bottomsheet_layout, null)

        val submit = view.findViewById<TextView>(R.id.submit_btn)
        val reviewTxt = view.findViewById<EditText>(R.id.review_txt)
        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)

        submit.setOnClickListener {
            val rating = ratingBar.rating.toString()
            val review = reviewTxt.text.toString()
            if(!rating.isEmpty()){
                if(!review.isEmpty()){
                    mAddReviewViewModel.addReview(
                        user_id.toString(),
                        rating.toString(),
                        review.toString(),
                        accessToken
                    )
                    loader.show()
                    dialog.dismiss()
                    finish()
                }else{
                    Toast.makeText(this,"Please provide your review.",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Rating is missing.",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun clickJobOverview(){
        binding.jobOverviewTv.setBackgroundResource(R.color.black)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.checkListTv.setBackgroundResource(R.color.white)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.relativeLay1.visible()
        binding.relativeLay2.gone()
    }

    private fun clickCheckList(){
        binding.checkListTv.setBackgroundResource(R.color.black)
        binding.checkListTv.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.jobOverviewTv.setBackgroundResource(R.color.white)
        binding.jobOverviewTv.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.relativeLay2.visible()
        binding.relativeLay1.gone()
    }

    private fun getCompletedJobsObserve(){
        mGetCompleteJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data?.success == true){
                        if(outcome.data?.data != null){
                            binding.shimmerView.gone()
                            binding.shimmerView.stopShimmer()
                            binding.mainLay.visible()
                            binding.statusTv.text = outcome.data!!.data[0].status.toString()
                            binding.jobTitleTv.text = outcome.data!!.data[0].title.toString()
                            binding.jobDescTv.text = outcome.data!!.data[0].description.toString()
                            binding.dateHtv.text = outcome.data!!.data[0].start_date.toString()+" to "+outcome.data!!.data[0].end_date.toString()
                            binding.timeTv.text = outcome.data!!.data[0].start_time.toString()+" - "+outcome.data!!.data[0].end_time.toString()
                            binding.priceTv.text = "$"+outcome.data!!.data[0].amount.toString()
                            binding.personCountTv.text = outcome.data!!.data[0].care_type
                            binding.locTv.text = outcome.data!!.data[0].address.toString()
                            user_id = outcome.data!!.data[0].job_accepted_by.user_id.toString()
                            caregiver_name = outcome.data!!.data[0].job_accepted_by.name.toString()
                            caregiver_photo = outcome.data!!.data[0].job_accepted_by.photo.toString()

                            var gen = ""
                            for(i in outcome.data!!.data[0].care_items){
                                if(gen.isEmpty()){
                                    gen = i.gender+": "+i.age
                                }else{
                                    gen = gen+", "+i.gender+": "+i.age
                                }
                            }
                            binding.personAgeTv.text = gen

                            binding.timerTv.text = LocalTime.MIN.plus(
                                Duration.ofMinutes( getDurationHour(
                                    parseDateToddMMyyyy("${outcome.data!!.data[0].end_date} ${outcome.data!!.data[0].end_time}")!!,
                                    getCurrentDate()
                                ) )
                            ).toString()

                            if(outcome.data!!.data[0].medical_history.isNotEmpty() && outcome.data!!.data[0].medical_history != null){
                                binding.medicalRecycler.visible()
                                binding.medicalHisHtv.visible()
                                medicalHistoryFillRecycler(outcome.data!!.data[0].medical_history.toMutableList())
                            }

                            outcome.data!!.data[0].experties?.let {
                                if(outcome.data!!.data[0].experties.isNotEmpty() && outcome.data!!.data[0].experties != null){
                                    binding.jobExpRecycler.visible()
                                    binding.jobExpHtv.visible()
                                    jobExpFillRecycler(outcome.data!!.data[0].experties.toMutableList())
                                }
                            }

                            if(outcome.data!!.data[0].other_requirements.isNotEmpty() && outcome.data!!.data[0].other_requirements != null){
                                binding.otherReqRecycler.visible()
                                binding.otherReqHtv.visible()
                                otherFillRecycler(outcome.data!!.data[0].other_requirements.toMutableList())
                            }

                            Glide.with(this).load(Constants.PUBLIC_URL+ outcome.data!!.data[0].job_accepted_by.photo)
                                .placeholder(R.color.color_grey)
                                .into(binding.profileImg)

                            if(outcome.data!!.data[0].check_list.isNotEmpty()){
                                binding.checkListRecycler.visible()
                                binding.noCheckListTv.gone()
                                checkListFillRecycler(outcome.data!!.data[0].check_list.toMutableList())
                            }else{
                                binding.noCheckListTv.visible()
                            }
                        }else{
                            Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        }
                        mGetCompleteJobViewModel.navigationComplete()
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

    private fun medicalHistoryFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.medicalRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompleteJobDetailsActivity)
        }
    }

    private fun jobExpFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.jobExpRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompleteJobDetailsActivity)
        }
    }

    private fun otherFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.otherReqRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompleteJobDetailsActivity)
        }
    }

    private fun checkListFillRecycler(list: MutableList<String>) {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.checkListRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = BulletPointAdapter(list,this@CompleteJobDetailsActivity)
        }
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

        return durationTotalMin.toLong()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return sdf.format(Date())
    }

    fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd h:mm a"
        val outputPattern = "dd-MM-yyyy HH:mm:ss"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    private fun addReviewObserver(){
        mAddReviewViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        mAddReviewViewModel.navigationComplete()
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

    private fun closeJobObserver(){
        mCloseJobViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
                        showReviewDialog()
                        mCloseJobViewModel.navigationComplete()
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