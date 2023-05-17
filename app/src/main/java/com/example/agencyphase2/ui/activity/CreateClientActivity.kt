package com.example.agencyphase2.ui.activity

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.agencyphase2.R
import com.example.agencyphase2.databinding.ActivityClientListBinding
import com.example.agencyphase2.databinding.ActivityCreateClientBinding
import com.example.agencyphase2.ui.fragment.ImagePreviewFragment
import com.example.agencyphase2.utils.UploadDocListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.InputStream

class CreateClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}