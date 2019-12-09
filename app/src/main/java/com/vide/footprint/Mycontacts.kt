package com.vide.footprint

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_mycontacts.*
import kotlinx.android.synthetic.main.contact_list_view.view.*
import android.view.LayoutInflater
import com.google.firebase.database.FirebaseDatabase


class Mycontacts : AppCompatActivity() {

    var listofDelContact = ArrayList<UserContact>()
    var listOfContact = ArrayList<UserContact>()
    var adapter:ContactAdapter?=null
    var contactAccessCode =123
    var item:MenuItem?= null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    var mAuth: FirebaseAuth? = null
    var userPhoneNumber:String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycontacts)
        mAuth = FirebaseAuth.getInstance()
        listOfContact = ArrayList<UserContact>()
//        dummyData()
        adapter = ContactAdapter(this, listOfContact)
        viewList.adapter =adapter
         userPhoneNumber= intent.getStringExtra("currentPhoneNumber")


        viewList.setOnItemLongClickListener(){ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->


//            println("index number $i and viewlist ${listofItems[i].itemDes}")
            view1.background =ContextCompat.getDrawable(applicationContext,R.drawable.background)
            println("i am done")

            item!!.setVisible(true)
//
//            del.visibility=View.VISIBLE
            del_item(listOfContact[i])

//            UserData.myContacts.remove(listOfContact[i].phoneNumber)
//            refreshData()
            true
        }

    }

    fun del_item(listofContact:UserContact){
       listofDelContact.add(listofContact)
    }

    inner class ContactAdapter :BaseAdapter{
        var listofContact:ArrayList<UserContact>? =null
        var context:Context? =null
        constructor(context:Context,listofContact:ArrayList<UserContact>):super(){
            this.listofContact =listofContact
            this.context =context
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var contact = listofContact!![p0]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView = inflator.inflate(R.layout.contact_list_view,null)
            myView.phoneNumberList.text = contact.phoneNumber
            myView.nameList.text = contact.name
            return myView
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




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator = menuInflater
        inflator.inflate(R.menu.contact_menu,menu)
        item= menu!!.findItem(R.id.del)
        return true

    }

    fun refreshData(){
        listOfContact.clear()

        for((key,value) in UserData.myContacts){
            listOfContact.add(UserContact(value,key))

        }


        adapter!!.notifyDataSetChanged()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.finishActivity ->{FirebaseAuth.getInstance().signOut()
                val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)}
//                FirebaseAuth.getInstance().signOut()}}
            R.id.addContact ->{
        checkPermission()
            }

            R.id.del ->{
                for (item in listofDelContact ){
                    UserData.myContacts.remove(item.phoneNumber)
                    refreshData()
                    myRef.child(item.phoneNumber!!).child("Finders").child(userPhoneNumber!!).removeValue()
                }

                listofDelContact.clear()
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
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
                        contactAccessCode)

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        contactAccessCode)

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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            contactAccessCode-> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    pickContact()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"We can not get accessed to the contact",Toast.LENGTH_LONG).show()
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

    val contactCode =123
    fun pickContact(){

    val intent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,contactCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode){
            contactCode ->{
                if(resultCode ==Activity.RESULT_OK){
                    val contact =data!!.data
                    println("onActivity result : $contact")
                    val cursor = contentResolver.query(contact!!,null,null,null,null)
                    cursor!!.moveToFirst()
                    val Id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val hasPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    println("On Activity Result $Id hasphone $hasPhoneNumber")
                    if(hasPhoneNumber.equals("1")){
                        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+Id,null,null)
                        phones!!.moveToFirst()
                        var phonenumber = phones.getString(phones.getColumnIndex("data1"))
                        phonenumber =UserData.formatPhoneNumber(phonenumber)
                        val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            UserData.myContacts.put(phonenumber, name)
                        refreshData()
                        myRef.child(userPhoneNumber!!).child("Finders").child(phonenumber).setValue(true)

                        println("userphone number $$$$$$$$$$$$$$$$$$$$4: $userPhoneNumber ")
                        myRef.child(phonenumber).child("Finders").child(userPhoneNumber!!).setValue(true)

                    }

        cursor.close()
                }
            }
            else->{
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


}
