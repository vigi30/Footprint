package com.vide.footprint

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_list_view.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    var userPhoneNumber:String? =null
    var listOfContact = ArrayList<UserContact>()
    var adapter: ContactAdapter?=null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    var currentuserlong:Double? =null
    var currentuserlat:Double? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPhoneNumber= intent.getStringExtra("currentPhoneNumber")

        adapter = ContactAdapter(this, listOfContact)
        viewListMain.adapter =adapter

        viewListMain.setOnItemClickListener(){adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->

            var contact = listOfContact[i]
            val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
            val date = Date()
            myRef.child(contact.phoneNumber!!).child("request").setValue(df.format(date).toString())
            Log.d("MainActivity","${contact.phoneNumber}")
            val intent = Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("contactPhoneNumber",contact.phoneNumber)
            intent.putExtra("currentPhoneNumber",userPhoneNumber)
            startActivity(intent)

        }


//        viewList.setOnItemLongClickListener(){ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
//
//
//            //            println("index number $i and viewlist ${listofItems[i].itemDes}")
//            view1.background = ContextCompat.getDrawable(applicationContext,R.drawable.background)
//            println("i am done")
//
//            item!!.setVisible(true)
////
////            del.visibility=View.VISIBLE
//            del_item(listOfContact[i])
//
////            UserData.myContacts.remove(listOfContact[i].phoneNumber)
////            refreshData()
//            true
//        }

    }


    var isAccessible =false
    override fun onResume() {
        super.onResume()

        refreshData()

        if(isAccessible) {return}
        checkPermission()

        checkLocationPermission()
    }
    fun refreshData(){

        myRef.child(userPhoneNumber!!).child("Finders").addValueEventListener(object :
            ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }


            override fun onDataChange(p0: DataSnapshot) {
               try {
                   val findersList = p0.value as HashMap<String, Any>
                   listOfContact.clear()

                   if (findersList == null) {
                       listOfContact.add(UserContact("Empty_Users", "Empty_phone"))
                       adapter!!.notifyDataSetChanged()
                       return
                   }
                   for (key in findersList.keys) {
                        val name = listOfContacts[key]
                       listOfContact.add(UserContact(name.toString(), key))
                   }
                   adapter!!.notifyDataSetChanged()

               }catch (ex:Exception){
                   listOfContact.clear()
                   listOfContact.add(UserContact("Empty_Users","Empty_phone"))
                   adapter!!.notifyDataSetChanged()
                   return
               }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator = menuInflater
        inflator.inflate(R.menu.main_menu,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addTracker ->{
                val intent = Intent(this, Mycontacts::class.java)
                intent.putExtra("currentPhoneNumber", userPhoneNumber)
                startActivity(intent)}
            R.id.logout ->{

                FirebaseAuth.getInstance().signOut()
                val intentHome = Intent(this, homeActivity::class.java)

                startActivity(intentHome)}


            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    inner class ContactAdapter : BaseAdapter {
        var listofContact:ArrayList<UserContact>? =null
        var context: Context? =null
        constructor(context: Context, listofContact:ArrayList<UserContact>):super(){
            this.listofContact =listofContact
            this.context =context
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var contact = listofContact!![p0]
            if(contact.name.equals("Empty_Users")){
                var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var myView = inflator.inflate(R.layout.activity_null_data,null)

                return myView
            }
            else{
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView = inflator.inflate(R.layout.contact_list_view,null)
            myView.phoneNumberList.text = contact.phoneNumber
            myView.nameList.text = contact.name
            return myView
            }
        }

        override fun getItem(p0: Int): Any {
            return listOfContact[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return listOfContact.size
        }

    }





    var listOfContacts=HashMap<String,String>()
    fun pickContact(){

        try{
            listOfContacts.clear()

            val cursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
            cursor!!.moveToFirst()
            do {
                val name=cursor!!.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                listOfContacts.put(UserData.formatPhoneNumber(phoneNumber),name)
            }while (cursor!!.moveToNext())
        }catch (ex:Exception){}
    }

    fun checkPermission(){

        if(Build.VERSION.SDK_INT>=23){


            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(this,"We need to access the contact services then only we will be able to the device location information",
                        Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        1)

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        1)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                pickContact()
            }
        }

    }
    fun checkLocationPermission(){

        if(Build.VERSION.SDK_INT>=23){


            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(this,"We need to access the location services then only we will be able to the device location information",
                        Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        2)

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        2)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                getUserLocation()
            }
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1-> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    pickContact()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"We can not get accessed to the contact", Toast.LENGTH_LONG).show()
                }
                return
            }
            2-> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    getUserLocation()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"WE can not get accessed to the location services",Toast.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    companion object{
        var location: Location? = null
    }



    inner class MyLocationServices : LocationListener {


        constructor():super(){
            isAccessible =true
            location = Location("Start")
            location!!.latitude =0.0
            location!!.longitude =0.0
        }
        override fun onLocationChanged(p0: Location?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            location = p0
            currentuserlat =location!!.latitude
            currentuserlong =location!!.longitude

            Log.d("MainActivity","inside class latitude:${location!!.latitude}")
            Log.d("MainActivity","inside class latitude:${location!!.longitude}")

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {

        }

    }
    fun getUserLocation(){

        var myLocation =  MyLocationServices()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0.0f,myLocation)
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0.0f,myLocation)
        }
//        Log.d("MainActivity","latitude:${location!!.latitude}")
//        Log.d("MainActivity","latitude:${location!!.longitude}")
//        myRef.child(userPhoneNumber!!).child("location").child("latitude").setValue(myLocation.location!!.latitude)
//        myRef.child(userPhoneNumber!!).child("location").child("longitude").setValue(myLocation.location!!.longitude)
//        myRef.child(userPhoneNumber!!).child("location").child("lastSeen").setValue(df.format(date).toString())

       myRef.child(userPhoneNumber!!).child("request").addValueEventListener(object : ValueEventListener{

           override fun onDataChange(p0: DataSnapshot) {

               if(location==null){return}
               else{
                   val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
                   val date = Date()
                   Log.d("MainActivity","latitude:${currentuserlat}")
                   Log.d("MainActivity","latitude:${currentuserlong}")
                   myRef.child(userPhoneNumber!!).child("location").child("latitude").setValue(currentuserlat)
                   myRef.child(userPhoneNumber!!).child("location").child("longitude").setValue(currentuserlong)
                   myRef.child(userPhoneNumber!!).child("location").child("lastSeen").setValue(df.format(date).toString())

               }
           }
           override fun onCancelled(p0: DatabaseError) {

           }


       })



    }
}
