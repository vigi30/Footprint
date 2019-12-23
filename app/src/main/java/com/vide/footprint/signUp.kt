package com.vide.footprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class signUp : homeActivity() {


    var imageAccessCode = 456
    var accessCode = 123
    var profileImage :Uri? =null
    var phoneNumber:String? =null
    var password:String? = null
    var emailId:String? = null
    var firstName:String? =  null
    var lastName:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

    }

    //onClick event called when user clicks on sign up button
    fun createAccount(view: View) {
        phoneNumber = phoneNumTxt.text.toString()
        password = passwordTxt.text.toString()
        emailId = emailTxt.text.toString()
        firstName = firstNameTxt.text.toString()
        lastName = lastNameTxt.text.toString()
       var  currentphone=createAccountFirebase(emailId!!, password!!,phoneNumber!!,firstName!!,lastName!!,profileImage)



    }

    //onClick event called when user clicks on Add profile picture button
    fun gallaryImage(view: View) {

        // to check the permission of the gallary i.e storage
        checkPermission()

    }

    // checking the permission of the services
    fun checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {


            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(
                        this,
                        "We need to access the gallery to upload the images",
                        Toast.LENGTH_LONG
                    ).show()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        accessCode
                    )

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        accessCode
                    )

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                loadImage()
            }
        }

    }

    // a callback method to handle the response from the user
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            accessCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    loadImage()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this,
                        "WE can not get accessed to the gallary",
                        Toast.LENGTH_LONG
                    ).show()
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

    // opening the gallary and selecting an image
    fun loadImage() {

        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, imageAccessCode)
    }

    // response from the user that is the image selected by the user is handled here
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageAccessCode && data != null && resultCode == RESULT_OK) {
            val selectedImage = data.data
            val cursor = contentResolver.query(selectedImage!!, null, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            var selectedfilename = cursor.getString(columnIndex)
            cursor.close()
//          testImage.setImageBitmap(BitmapFactory.decodeFile(picturePath))

            profilePicture.setImageURI(selectedImage)

//            addItemTxt.text.append(selectedfilename)
//            println("image has been added $selectedfilename")
            profileImage = selectedImage
            Toast.makeText(
                this,
                " \b $selectedfilename image has been added successfully",
                Toast.LENGTH_LONG
            ).show()


        }
    }
}