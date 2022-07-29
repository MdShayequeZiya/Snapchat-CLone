package com.example.snapchatclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapsActivity : AppCompatActivity() {

    var createSnapImageView:ImageView? = null
    var messageEditText:EditText? =null
    val imageName = UUID.randomUUID().toString() + ".jpg"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snaps)

        setTitle("Come on, You can choose one!")

        createSnapImageView= findViewById(R.id.createSnapsImageView)
        messageEditText = findViewById(R.id.messageEditText)

    }

    fun chooseImage(view: View){


        // checking ki pehle se koi permission hai ya nahi
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // agar nahi kiya hai permission grant to hum maange ge
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }

    }

    fun next(view: View){

        // next button click krne par chosen photo upload ho jaani chahiye

        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // yaha child create krege jo ki image ko store krne ke kaam aayega

       // FirebaseStorage.getInstance().getReference().child("Images").child(imageName)

        val mainrefs = FirebaseStorage.getInstance().getReference().child("Images").child(imageName)

        var uploadTask = mainrefs.putBytes(data)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads

            // upload fail ho jaaye to kya krna hai

            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()

        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Toast.makeText(this, " Upload Successful!", Toast.LENGTH_SHORT).show()


            var downloadUrl : String = ""
            val intent= Intent(this, ChooseUserActivity::class.java)

            mainrefs.downloadUrl.addOnCompleteListener(){
                taskSnapshot->

                val url = taskSnapshot.result
                downloadUrl = url.toString()


                intent.putExtra("imageUrl", downloadUrl)
                Log.i("Download Url:", downloadUrl)
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", messageEditText?.text.toString())

                startActivity(intent)

            }



        }


    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    // Jb koi permission grant krega to yeh function call hoga
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // getting the result from the media store
        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {

                //Getting image from the result
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}