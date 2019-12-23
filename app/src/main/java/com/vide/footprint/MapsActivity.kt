package com.vide.footprint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var contactPhoneNumber:String? =null
    var currentUserPhoneNumber:String? =null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        contactPhoneNumber = intent.getStringExtra("contactPhoneNumber")
        currentUserPhoneNumber = intent.getStringExtra("currentPhoneNumber")

        // to get the latitude and longitude information

        myRef.child(contactPhoneNumber!!).child("location").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            // when a data is changed in the location below call back method is invoked
            override fun onDataChange(p0: DataSnapshot) {
             val contactLocation = p0.value as HashMap<String,Any>
                val latitude =  contactLocation["latitude"] as Double
                val longitude =  contactLocation["longitude"] as Double
                 lastSeen = contactLocation["lastSeen"] as String
                MapsActivity.mapLocation = LatLng(latitude,longitude)
                // fragment intializes
                mapfragment()

                // calling the thread function
                var mythread = myThread()
                mythread.start()
            }


        })
    }

    // fragment declareation and intialization for representing map
    fun mapfragment(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    // a thread to display the the map

    inner class myThread : Thread{
        constructor():super(){

        }

        override fun run() {
            var localityName:String? = null
            while(true){
                try {
                    runOnUiThread {
                        mMap.clear()

                    // intializing the marker and move the camera to the coordinates

                        mMap.addMarker(MarkerOptions().position(mapLocation).title("$lastSeen"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapLocation))
                    }
                    Thread.sleep(1000)
                }
                catch (ex: Exception){}


            }
        }
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


// an object which contains information about the latitutde and longitude
    companion object{
        var mapLocation = LatLng(-34.0, 151.0)
        var lastSeen:String? =null
    }


    // a callback funtion for the map
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(mapLocation).title("$lastSeen"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapLocation))
    }
}
