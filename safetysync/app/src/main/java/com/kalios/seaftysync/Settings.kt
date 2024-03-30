package com.kalios.seaftysync

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class Settings : AppCompatActivity() {

    private lateinit var switch_location : Switch
    private lateinit var switch_microphone : Switch
    private lateinit var switch_photo : Switch
    private lateinit var switch_video : Switch
    private lateinit var switch_displayoverother : Switch
    private lateinit var switch_changeTheme : Switch
    //Icon
    private lateinit var location_icon : ImageView
    private lateinit var microphone_icon : ImageView
    private lateinit var photo_icon : ImageView
    private lateinit var video_icon : ImageView
    private lateinit var displayoverother_icon : ImageView
    private lateinit var theme_change_icon : ImageView
    private lateinit var voiceHelper: VoiceHelper
    private lateinit var smsHelper: SMSHelper


    // voice integration
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            Log.e(TAG, "Error listening: $error")
            // Release wake lock on error
            voiceHelper.stopListening()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                for (result in it) {
                    if (result.equals("help", ignoreCase = true)) {

                    }
                }
            }
            // Release wake lock after processing results
            voiceHelper.stopListening()
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    // onCreate Starts here
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
         //Switch Collection
        switch_location = findViewById(R.id.switch_location)
        switch_microphone = findViewById(R.id.switch_microphone)
        switch_photo = findViewById(R.id.switch_photo)
        switch_video = findViewById(R.id.switch_video)
        switch_displayoverother = findViewById(R.id.switch_display_over_other)
        switch_changeTheme = findViewById(R.id.theme_switch)


        // Data from Main activity
        val phoneNumbers: List<String> = intent.getStringArrayListExtra("phoneNumbers")?.toList() ?: emptyList()
        val message = intent.getStringExtra("message")


        // Icon Setup
        location_icon = findViewById(R.id.location_icon)
        microphone_icon = findViewById(R.id.microphone_icon)
        photo_icon = findViewById(R.id.photo_icon)
        video_icon = findViewById(R.id.video_icon)
        displayoverother_icon = findViewById(R.id.display_over_other_app_icon)
        theme_change_icon =findViewById(R.id.theme_change_icon)
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        voiceHelper = VoiceHelper(this)


        switch_location.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                location_icon.setImageResource(R.drawable.ic_location_on)
                Toast.makeText(this,"Location Permission Granted",Toast.LENGTH_LONG).show()
            } else{
                location_icon.setImageResource(R.drawable.ic_location_off)
                Toast.makeText(this,"Location Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
        switch_microphone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                microphone_icon.setImageResource(R.drawable.ic_microphone_on)
                voiceHelper.startListening(recognitionListener)
                if (message != null) {
                    smsHelper.sendSMS(phoneNumbers,message)
                }
                Toast.makeText(this,"Microphone Permission Granted",Toast.LENGTH_LONG).show()
            } else{
                microphone_icon.setImageResource(R.drawable.ic_microphone_off)
                voiceHelper.stopListening()
                Toast.makeText(this,"Microphone Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
        switch_photo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                photo_icon.setImageResource(R.drawable.ic_photo_on)
                Toast.makeText(this,"Photo Permission Granted",Toast.LENGTH_LONG).show()

            } else{
                photo_icon.setImageResource(R.drawable.ic_photo_off)
                Toast.makeText(this,"Photo Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
        switch_video.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                video_icon.setImageResource(R.drawable.ic_video_on)
                Toast.makeText(this,"Video Permission Granted",Toast.LENGTH_LONG).show()
            } else{
                video_icon.setImageResource(R.drawable.ic_video_off)
                Toast.makeText(this,"Video Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
        switch_displayoverother.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                displayoverother_icon.setImageResource(R.drawable.ic_over_others)
                Toast.makeText(this,"Display Over Other Apps Permission Granted",Toast.LENGTH_LONG).show()
            } else{
                displayoverother_icon.setImageResource(R.drawable.ic_over_others)
                Toast.makeText(this,"Display Over Other Apps Permission Denied",Toast.LENGTH_LONG).show()
            }
        }

        switch_changeTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                theme_change_icon.setImageResource(R.drawable.ic_dark_mode)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                theme_change_icon.setImageResource(R.drawable.ic_light_mode)
            }
        }
        switch_changeTheme.isChecked = isDarkModeActive()
    }
    companion object {
        private const val TAG = "Voice_Test"
    }
    private fun isDarkModeActive(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}