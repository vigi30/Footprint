package com.vide.footprint

import android.net.Uri

class UserData() :homeActivity(){

// A user related email and phone number will be stored.
    companion object    {
        var myContacts:MutableMap<String,String> =HashMap()

        // a function to format the phone numbers.
        fun formatPhoneNumber(phoneNumber:String):String {
            var Number= phoneNumber.replace("[^0-9]".toRegex(),"")


            return  Number
        }
    }

}