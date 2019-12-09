package com.vide.footprint

import android.net.Uri

class UserData() :homeActivity(){

    var phoneNumber:String? =null
    var password:String? = null
    var emailId:String? = null
    var firstName:String? =  null
    var lastName:String? = null
    var profileImage:Uri? = null

    constructor(phoneNumber:String,password:String,emailId:String,firstname:String,lastName:String,profileImage:Uri?) : this() {

        this.phoneNumber =phoneNumber
        this.password =password
        this.emailId =emailId
        this.firstName = firstname
        this.lastName = lastName
        this.profileImage =profileImage
    }


    fun curentUser():String{
        return mAuth!!.currentUser!!.phoneNumber!!
    }
    companion object    {
        var myContacts:MutableMap<String,String> =HashMap()
        fun formatPhoneNumber(phoneNumber:String):String {
            var onlyNumber= phoneNumber.replace("[^0-9]".toRegex(),"")
//            if (phoneNumber[0]== '+') {
//                onlyNumber ="+"+ phoneNumber
//            }

            return  onlyNumber
        }
    }

}