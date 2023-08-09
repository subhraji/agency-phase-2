package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.UpcomingListItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_upcomming_jobs.Data
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import com.example.agencyphase2.ui.activity.UpcommingJobDetailsActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class UpcomingJobsAdapter (private val itemList: List<Data>,
                           private val context: Context
):
    RecyclerView.Adapter<UpcomingJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingJobsAdapter.ViewHolder {
        val itemBinding = UpcomingListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpcomingJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: UpcomingJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: UpcomingListItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var gen: String = ""
        fun bind(data: Data, context: Context) {

            val width = context.resources.displayMetrics?.widthPixels
            if (width != null) {
                val params = RecyclerView.LayoutParams((width * 0.9).toInt(),ViewGroup.LayoutParams.MATCH_PARENT)
                params.setMargins(10, 10, 10, 10)
                itemView.layoutParams = params
            }

            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
                careTypeTv.text = data?.care_type
                addressTv.text = data?.short_address.toString()
                dateHtv.text = data?.start_date.toString()+"-"+data?.end_date.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()
                rootLay.setOnClickListener {
                    val intent = Intent(context, UpcommingJobDetailsActivity::class.java)
                    intent.putExtra("id",data?.job_id)
                    context.startActivity(intent)
                }
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.patient_name+", "+i.gender+": "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+": "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen

                statusTv.text = data.status.toString()

                timeLeftTv.text = "TIME LEFT : "+ LocalTime.MIN.plus(
                    Duration.ofMinutes( getDurationHour(
                        getCurrentDate(),
                        parseDateToddMMyyyy("${data.start_date} ${data?.start_time}")!!
                    ) )
                ).toString()
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

    }
}