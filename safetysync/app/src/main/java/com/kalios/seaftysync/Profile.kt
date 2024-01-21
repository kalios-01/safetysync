package com.kalios.seaftysync

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

class Profile : AppCompatActivity() {

    private lateinit var profile_preview : ImageView
    private lateinit var profile_img_chooser : Button
    private lateinit var username_edt : EditText
    private lateinit var update_profile :Button
    private lateinit var img_selected: Uri

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profile_preview = findViewById(R.id.profile_preview)
        profile_img_chooser = findViewById(R.id.profile_img_chooser)
        username_edt = findViewById(R.id.username_edt)
        update_profile = findViewById(R.id.update_profile)
        profile_img_chooser.setOnClickListener {
            openGallery()
        }
    }

    // function for opening Dialog
    val getAction = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it!=null) {
            img_selected =it
            profile_preview.setImageURI(img_selected)
        }
    }
    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getAction.launch("image/*")
    }
}