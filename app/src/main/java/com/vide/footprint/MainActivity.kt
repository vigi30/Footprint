package com.vide.footprint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    var userPhoneNumber:String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userPhoneNumber= intent.getStringExtra("currentPhoneNumber")
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
            R.id.help ->{

            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}
