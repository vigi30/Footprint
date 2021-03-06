package com.vide.footprint

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


open class homeActivity : AppCompatActivity() {
    var currentphonenumber: String? = null
    var currentEmail :String? =null

    //declare  Firebase Authentication reference
    var mAuth: FirebaseAuth? = null

    //declare and intialize Firebase real time database
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    var phone:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("homeActivity", "onCreate")
        setContentView(R.layout.activity_home)
        // Instantaite Authentication
        mAuth = FirebaseAuth.getInstance()

        Log.d("homeActivity", "onCreate $mAuth")
    }


    // onClick Event triggered when clicked on SignUp button
    fun signup(view: View) {

        // calling the SignUp activity
        var signupintent = Intent(this, signUp::class.java)
        startActivity(signupintent)
    }

    // onClick Event triggered when clicked on Log in button
    fun loginHome(view: View) {

        // calling the Login activity
        var loginintent = Intent(this, Login::class.java)
        startActivity(loginintent)
    }

    // Creating Account using Firebase Authentication methis
    fun createAccountFirebase(
        email: String,
        password: String,
        phoneNumber: String,
        firstName: String,
        lastName: String,
        profileImage: Uri?
    ) {  // method to create user using Email and password
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
                    val date = Date()
                    // Adding user information to the realtime databases
                    myRef.child(phoneNumber).child("request").setValue(df.format(date).toString())
                    myRef.child(phoneNumber).child("FirstName").setValue(firstName)
                    myRef.child(phoneNumber).child("LastName").setValue(lastName)
                    myRef.child(phoneNumber).child("Email-ID").setValue(email)
                    myRef.child(phoneNumber).child("Password").setValue(password)

//                    myRef.child(phoneNumber).child("profileImage").setValue(downloadUrl)
                    Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                    var user = mAuth!!.currentUser;
//                    Log.d("LOGIN", "${user!!.uid}");
//                    SaveImageIntoFirebase(email, phoneNumber)
                    loadmain(phoneNumber)

                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w("LOGIN", "createUserWithEmail:failure", task.exception);
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show();
//                        updateUI(null);
                }

            }

    }

    @SuppressLint("SimpleDateFormat")
    //Saving the image to the Firebase Storage
//    fun SaveImageIntoFirebase(email: String, phoneNumber: String) {
//        var currentUser = mAuth!!.currentUser
//        var du: String? = null
//        var du1: String? = null
//        var du3: String = "&alt=media"
//        // get the Firebase Storage instance
//        val storage = FirebaseStorage.getInstance()
//
//        // get the  Storage reference to upload, download , or delete a file.
//        val storageReference = storage.getReferenceFromUrl("gs://testfirebasedb-9269f.appspot.com/")
//        val df = SimpleDateFormat("ddMMyyHHmmss")
//        val dataobj = Date()
//        val imagePath = Splitemail(email) + "." + df.format(dataobj) + ".jpg"
//        val imageRef = storageReference.child("Images/" + imagePath)
////        profilePicture.isDrawingCacheEnabled =true
//
//        // converting the image to ByteArray and getting URL
//        val url = imageRef.downloadUrl
//        Log.d("uploadImage", "$url")
//        println("uploadImage $url")
//        val drawable = profilePicture.drawable as BitmapDrawable
//        val bitmap = drawable.bitmap
//        val bytearray = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearray)
//        val data = bytearray.toByteArray()
//        val uploadTask = imageRef.putBytes(data)
//        var uri: Uri
////        uploadTask.addOnSuccessListener { t ->
////            t.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
////                uri = task.result!!
//////                myRef.child(phoneNumber).child("profileImage").setValue(uri)
////
////            }
////        }
//
//
//    }

    // Getting
//    fun Splitemail(email: String): String {
//        val split = email.split("@")
//        return split[0]
//

    //Authenticate the existing user for sign in
    fun loginTofirebase(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//        Log.d(FragmentActivity.TAG, "signInWithEmail:success")


                    val user = mAuth!!.getCurrentUser()
                    // getting the current user phone number
//                    getLoginPhoneNumber(email)
                    getPhoneNumber(email)

                } else {
                    // If sign in fails, display a message to the user.
//        Log.w(FragmentActivity.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                // ...
            }
    }


    // loading the Main activity
    fun loadmain(currentPhoneNumber: String?) {

        //checking whether there any current user such that no need to login again and again.
        mAuth!!.addAuthStateListener {
            var currentuser = mAuth!!.currentUser
            if (currentuser != null) {
                Log.d("homeActivity", " loadmain() :$currentuser ")
                // save in database
//            myRef.setValue("Hello, World!")
//            myRef.child("Users").child(currentuser.uid).setValue(currentuser.email)

//                myRef.child(currentPhoneNumber!!).child("request").setValue(df.format(date).toString())
                println("user phone number <><><<><><><><><><><<< inside loadmain :${mAuth!!.currentUser!!.email} ")
                var intent = Intent(this, MainActivity::class.java)

                intent.putExtra("currentPhoneNumber", currentPhoneNumber)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

    }


    //checking whether there any current user such that no need to login again and again.
    override fun onStart() {
        super.onStart()
        var curr = mAuth!!.currentUser
        if(curr!=null){
            getPhoneNumber(curr.email)
        }

        Log.d("homeActivity", " onStart : ")
//        loadmain(currentphonenumber)
    }

    // getting the current user phone number
    fun getPhoneNumber(email_id: String?) {
        var ph_no: String? = null
        // Adding listener event to the database reference
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }
        // when anyt data changes within database below function is called.
            override fun onDataChange(p0: DataSnapshot) {

                for (data in p0.children) {

                    if (data.child("Email-ID").getValue()!!.equals(email_id)) {

                        // get the parent key
                        currentphonenumber = data.key.toString()
                        loadmain(currentphonenumber)
                    }
                }

            }


        })


    }
    // getting the current user phone number ,called when sign In is invoked
    fun getLoginPhoneNumber(email_id: String?) {
        var ph_no: String? = null
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (data in p0.children) {

                    if (data.child("Email-ID").getValue()!!.equals(email_id)) {


                        currentphonenumber = data.key.toString()
                        loadmain(currentphonenumber)
                        returnPhoneNumber(currentphonenumber!!)
                    }
                }

            }


        })


    }

        // get the current phone of the user
    fun returnPhoneNumber(ph_no:String){
        phone = ph_no

    }



}
