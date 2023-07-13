package com.example.agencyphase2.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.agencyphase2.R
import com.example.agencyphase2.adapter.MessageListAdapter
import com.example.agencyphase2.databinding.ActivityChatBinding
import com.example.agencyphase2.model.pojo.chat.ChatModel
import com.example.agencyphase2.model.pojo.chat.ChatRequest
import com.example.agencyphase2.model.pojo.chat.ChatSeenRequested
import com.example.agencyphase2.model.pojo.chat.Data
import com.example.agencyphase2.utils.Constants
import com.example.agencyphase2.utils.PrefManager
import com.example.agencyphase2.utils.SocketHelper
import com.google.gson.Gson
import com.user.caregiver.gone
import com.user.caregiver.hideSoftKeyboard
import com.user.caregiver.visible
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var mMessageAdapter: MessageListAdapter
    private var mSocket: Socket? = null
    val list: MutableList<ChatModel> = mutableListOf()
    private var job_id: String? = null

    private lateinit var caregiver_id: String
    private lateinit var userId: String
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            caregiver_id = intent?.getStringExtra("caregiver_id").toString()
            val name = intent?.getStringExtra("name")
            val photo = intent?.getStringExtra("photo")
            job_id = intent?.getStringExtra("job_id")

            binding.chatFrgPhoneNoTxt.text = name
            Glide.with(this).load(Constants.PUBLIC_URL+photo)
                .placeholder(R.color.color_grey)
                .into(binding.userImg)
            binding.chatWithTv.text = "Chat with ${name}"
        }

        //get token
        accessToken = "Bearer "+PrefManager.getKeyAuthToken()

        userId = PrefManager.getUserId().toString()

        binding.chatFrgBackArrow.setOnClickListener {
            finish()
        }

        mMessageAdapter = MessageListAdapter(mutableListOf(), this)

        fillChatRecycler()

        isMsgAvailAble()

        binding.chatBtnSend.setOnClickListener {
            hideSoftKeyboard()
            val messageText = binding.textInput.text.toString().trim()
            if (messageText.isEmpty()) {
                binding.textInput.error = "message cannot be empty"
            }else{
                val currentThreadTimeMillis = System.currentTimeMillis()
                val msgUuid = currentThreadTimeMillis.toString()

                //send chat msg
                val sendMsg = ChatRequest(
                    messageText,
                    PrefManager.getUserId().toString(),
                    caregiver_id,
                    getCurrentTime(),
                    "",
                    msgUuid,
                    job_id!!,
                    accessToken
                )
                attemptSend(sendMsg)

                //save chat msg on list to show on recyclerview
                val message = ChatModel(
                    msgUuid,
                    messageText,
                    "",
                    getCurrentTime(),
                    true
                )
                mMessageAdapter.addMessage(message)
                binding.textInput.text = null
                scrollToLast()
                isMsgAvailAble()
            }
        }

        initSocket()
    }

    private fun isMsgAvailAble() {
        if(mMessageAdapter.itemCount == 0){
            binding.chatWithCard.visible()
        }else{
            binding.chatWithCard.gone()
        }
    }

    private fun initSocket(){
        try {
            mSocket = IO.socket(Constants.NODE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket?.on("receiveMessage", onNewMessage);
        mSocket?.on("messageAck", ackStatusListener);
        mSocket?.connect()

        //delay(10L)
        mSocket!!.emit("signin", PrefManager.getUserId())
    }

    private fun attemptSend(message: ChatRequest) {
        val gson = Gson()
        try {
            val obj = JSONObject(gson.toJson(message))
            mSocket!!.emit("sendMessage", obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun attemptSendSeen(request: ChatSeenRequested) {
        val gson = Gson()
        try {
            val obj = JSONObject(gson.toJson(request))
            mSocket!!.emit("isMessageSeen", obj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            this@ChatActivity.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                var image: String? = null
                val msg: String
                val time: String

                try {
                    val messageData = data.getJSONObject("chatResponse")
                    val message = Gson().fromJson(messageData.toString(), Data::class.java)
                    msg = message.msg
                    image = message.image
                    time = message.time
                    if(!image.isEmpty() && image != null){
                        val chat = ChatModel(
                            message.messageId,
                            msg,
                            image,
                            time,
                            false
                        )
                        mMessageAdapter.addMessage(chat)
                        scrollToLast()
                        isMsgAvailAble()
                    }else{
                        val chat = ChatModel(
                            message.messageId,
                            msg,
                            "",
                            time,
                            false
                        )
                        mMessageAdapter.addMessage(chat)

                        scrollToLast()
                        isMsgAvailAble()
                    }

                    //send seen ack
                    val sendSeen = ChatSeenRequested(
                        message.messageId,
                        caregiver_id
                    )
                    attemptSendSeen(sendSeen)

                } catch (e: JSONException) {
                    return@Runnable
                }

            })
        }
    }

    private val ackStatusListener: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            this@ChatActivity.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                try {
                    val msgId = data.getString("messageId")
                    val seenStatus = data.getString("messageSeen")
                    mMessageAdapter.updateSeen(msgId)
                    Toast.makeText(this@ChatActivity, "seen => ${msgId}", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    return@Runnable
                }
            })
        }
    }

    private fun getCurrentTime(): String{
        val sdf = SimpleDateFormat("hh:mm a")
        return sdf.format(Date())
    }

    private fun fillChatRecycler() {
        val gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.chatRecycler.apply {
            layoutManager = gridLayoutManager
            adapter = mMessageAdapter
        }
    }

    private fun scrollToLast(){
        binding.chatRecycler.scrollToPosition((binding.chatRecycler.adapter?.itemCount ?: 1) - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}