package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText : EditText? = null
    var passwordEditText : EditText? = null

    // declare variable useful for the firebase access!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setting up the variables
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText= findViewById(R.id.passwordEditText)

        // providing value in the variable declared for firebase
        // Initialize Firebase Auth
        auth = Firebase.auth

        if(auth.currentUser!=null){
            logIn()
        }


    }

    fun goClicked(view : View){

        // go clicked login ya sign up kro
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())      // yaha question mark isiliye laga hai kyun ki surity nahi ki value aayega hi waha se
                .addOnCompleteListener(this){
                    task ->
                    if(task.isSuccessful){
                        // mtlb login kr rha hai user
                        logIn()
                    }else{
                        //sign up krega user
                        auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                                .addOnCompleteListener(this){
                                    task ->
                                    if(task.isSuccessful){
                                        // mtlb sign up ho gya, ab login kro user ko

                                        FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid).child("email").setValue(emailEditText?.text.toString())

                                        logIn()
                                    }else{
                                        // mtlb kuch to garbar ho gya hai toast message show kro
                                        Toast.makeText(applicationContext, "Hey, something went wrong!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    }
                }

    }

    fun logIn (){

        // for login purpose going to next activity: Which is snaps acitvity

        // creation of intent
        val intent = Intent(this, SnapsAcitivity::class.java)
        startActivity(intent)
    }

}
