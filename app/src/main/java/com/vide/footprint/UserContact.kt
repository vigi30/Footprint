package com.vide.footprint


// a class for storing the details of the contacts that a user going to track
class UserContact {
    var name : String?= null
    var phoneNumber:String? = null
    constructor(name:String, phoneNumber:String){
        this.name = name
        this.phoneNumber = phoneNumber
    }
}