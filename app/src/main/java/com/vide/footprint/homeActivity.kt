package com.vide.footprint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class homeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    fun signup(view: View){

        var signupintent = Intent(this,signUp::class.java)
        startActivity(signupintent)
    }



    fun login(view:View){

        var loginintent = Intent(this, Login::class.java)
        startActivity(loginintent)
    }

}
