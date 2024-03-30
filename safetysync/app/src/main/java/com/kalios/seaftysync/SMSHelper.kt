package com.kalios.seaftysync


import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SMSHelper(private val activity: AppCompatActivity) {

    fun sendSMS(phoneNumbers: List<String>, message: String) {
        val smsManager = SmsManager.getDefault()
        for (phoneNumber in phoneNumbers) {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
        Toast.makeText(activity, "SMS sent successfully", Toast.LENGTH_SHORT).show()
    }
}
