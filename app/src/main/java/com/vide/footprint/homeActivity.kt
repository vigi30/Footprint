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
    var mAuth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    var phone:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("homeActivity", "onCreate")
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()

        Log.d("homeActivity", "onCreate $mAuth")
    }

    fun signup(view: View) {

        var signupintent = Intent(this, signUp::class.java)
        startActivity(signupintent)
    }


    fun loginHome(view: View) {

        var loginintent = Intent(this, Login::class.java)
        startActivity(loginintent)
    }

    fun createAccountFirebase(
        email: String,
        password: String,
        phoneNumber: String,
        firstName: String,
        lastName: String,
        profileImage: Uri?
    ) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
                    val date = Date()
                    myRef.child(phoneNumber).child("request").setValue(df.format(date).toString())
                    myRef.child(phoneNumber).child("FirstName").setValue(firstName)
                    myRef.child(phoneNumber).child("LastName").setValue(lastName)
                    myRef.child(phoneNumber).child("Email-ID").setValue(email)
                    myRef.child(phoneNumber).child("Password").setValue(password)

//                    myRef.child(phoneNumber).child("profileImage").setValue(downloadUrl)
                    Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                    var user = mAuth!!.currentUser;
//                    Log.d("LOGIN", "${user!!.uid}");
                    SaveImageIntoFirebase(email, phoneNumber)
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
    fun SaveImageIntoFirebase(email: String, phoneNumber: String) {
        var currentUser = mAuth!!.currentUser
        var du: String? = null
        var du1: String? = null
        var du3: String = "&alt=media"
        // get the Firebase Storage instance
        val storage = FirebaseStorage.getInstance()

        // get the  Storage reference to upload, download , or delete a file.
        val storageReference = storage.getReferenceFromUrl("gs://testfirebasedb-9269f.appspot.com/")
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = Splitemail(email) + "." + df.format(dataobj) + ".jpg"
        val imageRef = storageReference.child("Images/" + imagePath)
//        profilePicture.isDrawingCacheEnabled =true
        val url = imageRef.downloadUrl
        Log.d("uploadImage", "$url")
        println("uploadImage $url")
        val drawable = profilePicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytearray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytearray)
        val data = bytearray.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        var uri: Uri
//        uploadTask.addOnSuccessListener { t ->
//            t.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
//                uri = task.result!!
////                myRef.child(phoneNumber).child("profileImage").setValue(uri)
//
//            }
//        }


    }

    fun Splitemail(email: String): String {
        val split = email.split("@")
        return split[0]
    }


    fun loginTofirebase(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//        Log.d(FragmentActivity.TAG, "signInWithEmail:success")


                    val user = mAuth!!.getCurrentUser()
                    getLoginPhoneNumber(email)
                    getPhoneNumber(email)
//                    val df = SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
//                    val date = Date()
////                    myRef.child(phone!!).child("request").setValue(df.format(date).toString())


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

    fun loadmain(currentPhoneNumber: String?) {

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



    override fun onStart() {
        super.onStart()
        var curr = mAuth!!.currentUser
        if(curr!=null){
            getPhoneNumber(curr.email)
        }

        Log.d("homeActivity", " onStart : ")
//        loadmain(currentphonenumber)
    }

    fun getPhoneNumber(email_id: String?) {
        var ph_no: String? = null
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (data in p0.children) {

                    if (data.child("Email-ID").getValue()!!.equals(email_id)) {


                        currentphonenumber = data.key.toString()
                        loadmain(currentphonenumber)
                    }
                }

            }


        })


    }
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


    fun returnPhoneNumber(ph_no:String){
        phone = ph_no

    }



}
