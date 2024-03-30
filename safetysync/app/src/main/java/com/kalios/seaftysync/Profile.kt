package com.kalios.seaftysync

import DataHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Profile : AppCompatActivity() {

    private lateinit var profile_preview : ImageView
    private lateinit var username_edt : EditText
    private lateinit var update_profile :Button
    private lateinit var img_selected: Uri

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profile_preview = findViewById(R.id.profile_preview)
        username_edt = findViewById(R.id.username_edt)
        update_profile = findViewById(R.id.update_profile)
        profile_preview.setOnClickListener {
            openGallery()
        }
        val dataHelper = DataHelper(this)
        // Profile Update
        val defaultDrawableIconUri = "android.resource://${packageName}/${R.drawable.ic_profile}"
        val bitmap_defaultDrawable = getBitmapFromDrawableUri(this, defaultDrawableIconUri)
        val defaultUsername = "User"
        CoroutineScope(Dispatchers.Main).launch {
            val updatedImage = dataHelper.retrieveBitmap()
            val updatedUsername = dataHelper.retrieveUsername()
            val finalImage = updatedImage ?: bitmap_defaultDrawable
            val finalUsername = updatedUsername ?: defaultUsername
            // Update UI with the new data
            username_edt.hint = finalUsername
            profile_preview.setImageBitmap(finalImage)
        }

        update_profile.setOnClickListener {
            val inputStream = contentResolver.openInputStream(img_selected)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            CoroutineScope(Dispatchers.Main).launch {
                dataHelper.saveData(bitmap, username_edt.text.toString())
            }
            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
            // Start the MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // function for opening Dialog
    private val getAction = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it!=null) {
            img_selected =it
            profile_preview.setImageURI(img_selected)
        }
    }
    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getAction.launch("image/*")
    }
    fun getBitmapFromDrawableUri(context: Context, drawableUri: String): Bitmap? {
        // Convert the drawable URI string into Uri object
        val uri = Uri.parse(drawableUri)
        try {
            // Decode the URI into a Bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}