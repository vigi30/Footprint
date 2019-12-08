package com.vide.footprint

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

open class homeActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()
    }
    fun signup(view: View){

        var signupintent = Intent(this,signUp::class.java)
        startActivity(signupintent)
    }



    fun loginHome(view:View){

        var loginintent = Intent(this, Login::class.java)
        startActivity(loginintent)
    }

    fun createAccountFirebase(email: String, password: String,phoneNumber:String,firstName:String,lastName:String,profileImage:Uri?) {
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

                    Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                    var user = mAuth!!.currentUser;
//                    Log.d("LOGIN", "${user!!.uid}");
                    SaveImageIntoFirebase(email,phoneNumber)
                    loadmain()

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

    fun SaveImageIntoFirebase(email:String,phoneNumber: String){
        var currentUser = mAuth!!.currentUser

        val storage  = FirebaseStorage.getInstance()
        val storageReference = storage.getReference("gs://testfirebasedb-9269f.appspot.com/Images")
        val df =SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = Splitemail(email)+"."+df.format(dataobj)+".jpg"
        val imageRef = storageReference.child("Images/"+imagePath)
        val drawable =profilePicture.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val bytearray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytearray)
        val data = bytearray.toByteArray()
        val upload = imageRef.putBytes(data)
        upload.addOnFailureListener{
            Toast.makeText(
                this, "Failed to upload .",
                Toast.LENGTH_SHORT
            ).show();
        }.addOnSuccessListener { taskSnapshot ->
            var url =  taskSnapshot.metadata!!.reference!!.downloadUrl;
            myRef.child(phoneNumber).child("Image").setValue(url)
        }
    }

    fun Splitemail(email:String):String{
        val split   = email.split("@")
        return split[0]
    }



    fun loginTofirebase(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//        Log.d(FragmentActivity.TAG, "signInWithEmail:success")
                    val user = mAuth!!.getCurrentUser()
                    loadmain()
                } else {
                    // If sign in fails, display a message to the user.
//        Log.w(FragmentActivity.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.Please Try Again",
                        Toast.LENGTH_SHORT).show()

                }

                // ...
            }
    }
    fun loadmain(){
        var currentuser = mAuth!!.currentUser;
        if(currentuser!=null){

            // save in database
//            myRef.setValue("Hello, World!")
//            myRef.child("Users").child(currentuser.uid).setValue(currentuser.email)
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email",currentuser.email)
            intent.putExtra("uid",currentuser.uid)

            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        loadmain()
    }
}
