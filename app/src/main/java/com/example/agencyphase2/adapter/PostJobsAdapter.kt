package com.example.agencyphase2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.PostJobsItemLayoutBinding
import com.example.agencyphase2.model.pojo.get_post_jobs.DataX
import com.example.agencyphase2.ui.activity.PostJobsDetailsActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class PostJobsAdapter (private val itemList: MutableList<DataX>,
                       private val context: Context):
    RecyclerView.Adapter<PostJobsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostJobsAdapter.ViewHolder {
        val itemBinding = PostJobsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostJobsAdapter.ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun add(jobs: List<DataX>) {
        itemList.addAll(jobs)
        notifyItemInserted(itemList.size-1)
    }

    override fun onBindViewHolder(holder: PostJobsAdapter.ViewHolder, position: Int) {

        val rowData = itemList[position]
        holder.bind(rowData, context)
    }

    class ViewHolder(private val itemBinding: PostJobsItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private lateinit var gen:String
        fun bind(data: DataX, context: Context) {

            itemBinding.apply {
                jobTitleTv.text = data?.title.toString()
                careTypeTv.text = data?.care_items.size.toString()+" "+data?.care_type
                addressTv.text = data?.short_address.toString()
                dateHtv.text = data?.date.toString()
                hourHtv.text = data?.start_time+" - "+data?.end_time
                priceTv.text = "$"+data?.amount.toString()
                rootLay.setOnClickListener {
                    val intent = Intent(context, PostJobsDetailsActivity::class.java)
                    intent.putExtra("id",data?.id)
                    context.startActivity(intent)
                }
                gen = ""
                for(i in data?.care_items){
                    if(gen.isEmpty()){
                        gen = i.gender+": "+i.age+" Yrs"
                    }else{
                        gen = gen+", "+i.gender+": "+i.age+" Yrs"
                    }
                }
                ageTv.text = gen

                //statusTv.text = data.status.toString()

                if(data?.status == "Bidding Started"){
                    statusTv.text = "Bidding Started"
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_yellow)))

                }else if(data?.status == "Quick Call"){
                    statusTv.text = "Quick \u00A0 \u00A0 Call"
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.error_red)))
                }else if(data?.status == "Open Job"){
                    statusTv.text = "Open \u00A0 \u00A0 Job "
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.color_green)))
                }else if(data?.status == "Upcoming"){
                    statusTvLay.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                    timeLeftTv.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(R.color.theme_blue)))
                    statusTv.text = "Upcoming"
                }

                timeLeftTv.text = "TIME LEFT : "+ LocalTime.MIN.plus(
                    Duration.ofMinutes( getDurationHour(
                        getCurrentDate(),
                        parseDateToddMMyyyy("${data.date} ${data?.start_time}")!!
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