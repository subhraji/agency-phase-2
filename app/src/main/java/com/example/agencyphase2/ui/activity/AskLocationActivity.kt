package com.example.agencyphase2.ui.activity

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityAskLocationBinding
import com.example.agencyphase2.model.repository.Outcome
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.viewmodel.UpdatePaymentStatusViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.user.caregiver.isConnectedToInternet
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class AskLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAskLocationBinding
    private val mUpdatePaymentStatusViewModel: UpdatePaymentStatusViewModel by viewModels()
    private lateinit var accessToken: String

    var date:String = ""
    var startTime:String = ""
    var endTime:String = ""

    private var startDate: String = ""

    private var startDateTime: String = ""
    private var endDateTime: String = ""

    private var durationHour: Int = 0
    private var durationDay: Int = 0
    private var durationTotalMin: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAskLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        binding.useLocBtn.setOnClickListener {
            showDateTimeBottomSheet()
        }

        binding.getDurationBtn.setOnClickListener {
            startDateTime = startDate + " " + startTime
            endDateTime = startDate + " " + endTime
            Toast.makeText(this,"clicked...",Toast.LENGTH_SHORT).show()
            getDurationHour(startDateTime, endDateTime)
        }

        //observe
        updatePaymentStatusObserve()

        mUpdatePaymentStatusViewModel.updatePaymentStatus(
            16,
            110.00,
            "vxss_hHgdbcv",
            10.00,
            10,
            10.00,
            1,
            accessToken
        )
    }

    private fun getDurationHour(startDateTime: String, endDateTime: String) {

        val sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        try {
            val d1: Date = sdf.parse(startDateTime)
            val d2: Date = sdf.parse(endDateTime)

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


            Toast.makeText(this,"clicked...${difference_In_Hours}",Toast.LENGTH_SHORT).show()

        }

        // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun showDateTimeBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.select_date_time_bottomsheet, null)

        val btnSave = view.findViewById<TextView>(R.id.save_btn)
        val btnClear = view.findViewById<ImageView>(R.id.clear_btn)
        val startTimeBtn = view.findViewById<RelativeLayout>(R.id.start_time_btn)
        val endTimeBtn = view.findViewById<RelativeLayout>(R.id.end_time_btn)
        val startTimeTv = view.findViewById<TextView>(R.id.start_time_txt)
        val endTimeTv = view.findViewById<TextView>(R.id.end_time_txt)


        btnClear.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            /*binding.helloHtv.text = getCurrentDate(datePicker)
            date = getCurrentDate(datePicker).toString()
            startDate = getDurationCurrentDate(datePicker).toString()*/
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

    fun getDurationCurrentDate(picker: DatePicker): String? {
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
        builder.append(month.toString() + "-")
        builder.append(picker.getYear())

        return builder.toString()
    }

    private fun updatePaymentStatusObserve(){
        mUpdatePaymentStatusViewModel.response.observe(this, Observer { outcome ->
            when(outcome){
                is Outcome.Success ->{
                    //loader.dismiss()
                    if(outcome.data?.success == true){
                        Toast.makeText(this,outcome.data!!.message, Toast.LENGTH_SHORT).show()
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