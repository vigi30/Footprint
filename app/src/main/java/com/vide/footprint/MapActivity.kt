package com.vide.footprint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MapActivity : homeActivity() , OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var accessCode =123
    var contactPhoneNUmber:String?=null
    var userPhoneNumber:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        contactPhoneNUmber = intent.getStringExtra("contactPhoneNumber")
        userPhoneNumber = intent.getStringExtra("currentPhoneNumber")
//        val contactPhoneNUmber = intent.getStringExtra("contactPhoneNumber")
        Log.d("MapActivity ","contactPhoneNumber : $contactPhoneNUmber")
        myRef.child(contactPhoneNUmber!!).child("location").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val contactInfo = p0.value as HashMap<String, Any>
                    var latitude = contactInfo["latitude"].toString()
                    var longitude = contactInfo["longitude"].toString()
                    Log.d("MapActivity ","longitude : $latitude")
                    Log.d("MapActivity ","latitude : $longitude")
                    MapActivity.lastSeen = contactInfo["lastSeen"].toString()

                    MapActivity.contactCoordinate = LatLng(latitude.toDouble(), longitude.toDouble())
                  load()

                } catch (ex: Exception) {
                }

            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


    }
    fun load(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    companion object{
        var contactCoordinate = LatLng(-34.0, 151.0)
        var lastSeen = "UnKnown"
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        var mythread = myThread()
        mythread.start()

    }

    inner class myThread : Thread{
        constructor():super(){

        }
        override fun run() {
            var localityName:String? = null
            while(true){
                try {
                    runOnUiThread {
                        mMap.clear()

//
//
//                        var sydney = LatLng(latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions().position(contactCoordinate).title(lastSeen))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(contactCoordinate))
                    }
                    Thread.sleep(1000)
                }
                catch (ex: Exception){}


            }
        }
    }




}
