package com.vide.footprint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast
//import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.Intent
import android.util.Log


class Login : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    fun login(view:View){
        var email = emailIdLogin.text.toString()
        var password = passwordLogin.text.toString()
        logintofirebase(email,password)
    }
    fun logintofirebase(email: String, password: String) {
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
            var intent = Intent(this, MapActivity::class.java)
            intent.putExtra("email",currentuser.email)
            intent.putExtra("uid",currentuser.uid)

            startActivity(intent)
        }
    }
}