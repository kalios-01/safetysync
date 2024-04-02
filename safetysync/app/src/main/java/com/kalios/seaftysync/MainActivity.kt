package com.kalios.seaftysync

import DataHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),ContactAdapter.OnItemClickListener, OnMapReadyCallback{

    private lateinit var add_contact : TextView
    private lateinit var contactlist:RecyclerView
    private lateinit var Edt_Number:EditText
    private lateinit var Btn_panic:Button
    private lateinit var location_view:TextView
    private lateinit var locationText : String
    private lateinit var locationlink : String
    //Recyclerview
    private var data = mutableListOf<Contact>()
    private val adapter = ContactAdapter(data,this)
    private lateinit var rv_Contact_Number :String
    private lateinit var Username : String
    // SMS
    private lateinit var message :String
    private var PhoneNumbers_List = mutableListOf<String>()
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 100
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
// Location
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Option Menu
    private lateinit var btn_option_menu : ImageButton
    //Profile & username
    private lateinit var profile_imageView : ImageView

    private lateinit var smsHelper: SMSHelper


    @SuppressLint("MissingInflatedId", "SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactlist = findViewById(R.id.rv_contact_list)
        location_view = findViewById(R.id.locationview)
        Btn_panic = findViewById(R.id.btn_panic)
        contactlist.layoutManager = LinearLayoutManager(this)
        contactlist.adapter = adapter
        add_contact = findViewById(R.id.add_contact)
        Edt_Number = findViewById(R.id.edt_addcontact)
        smsHelper = SMSHelper(this)

        // Profile Update
        val defaultDrawableIconUri = "android.resource://${packageName}/${R.drawable.ic_profile}"
        val bitmap_defaultDrawable = getBitmapFromDrawableUri(this, defaultDrawableIconUri)
        val dataHelper = DataHelper(this)
        CoroutineScope(Dispatchers.Main).launch {
            Username = dataHelper.retrieveUsername().toString();
            val updatedImage = dataHelper.retrieveBitmap()
            val finalImage = updatedImage ?: bitmap_defaultDrawable
            profile_imageView = findViewById(R.id.profile_imageView)
            profile_imageView.setImageBitmap(finalImage)
        }


        add_contact.setOnClickListener {
            rv_Contact_Number=Edt_Number.text.toString()
            if (rv_Contact_Number.isEmpty()){
                Toast.makeText(this, "Enter Number First",Toast.LENGTH_SHORT).show()
            }
            else{
                PhoneNumbers_List.add(rv_Contact_Number)
                data.add(Contact(rv_Contact_Number))
                adapter.notifyDataSetChanged()
                Edt_Number.text.clear()
            }
        }
        Btn_panic.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, send SMS directly
                smsHelper.sendSMS(PhoneNumbers_List,message)
            }
        }
        // Map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Initialize the MapView
        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, get user's location
            getCurrentLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        // Option Menu
        btn_option_menu= findViewById(R.id.option_menu)
        btn_option_menu.setOnClickListener {
            showOptionsMenu(it)
        }
    }
// POP UP MENU
    private fun showOptionsMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Profile::class.java))
                    // Handle theme menu item click
                    true
                }
                R.id.menu_settings -> {
                    Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Settings::class.java)
                    intent.putStringArrayListExtra("phoneNumbers", ArrayList(PhoneNumbers_List))
                    intent.putExtra("message", message)
                    startActivity(intent)
                    // Handle permission menu item click
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }




    // Permission Gain
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                smsHelper.sendSMS(PhoneNumbers_List,message)
            } else {
                // Permission denied, handle the denial gracefully
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get user's location
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied. Unable to show your location on the map.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        // Get the last known location from the FusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Create a LatLng object from the location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    locationText = "Latitude: ${currentLatLng.latitude} | Longitude: ${currentLatLng.longitude}"
                    locationlink = "https://www.google.com/maps?q=${currentLatLng.latitude},${currentLatLng.longitude}"
                    message = "Help Me \n $Username \n Location: \n $locationlink"
                    location_view.text =locationText

                    // Move the camera to the user's location and add a marker
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    map.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
                } else {
                    Toast.makeText(this, "Unable to retrieve your location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    // drawable to Bitmap Converter
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
    // Handle the lifecycle of the MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onItemClick(contact: Contact) {
        val removedContactIndex = data.indexOf(contact)
        if (removedContactIndex != -1) {
            adapter.removeItem(removedContactIndex)
            PhoneNumbers_List.remove(contact.Number)
        }
    }
}