package com.example.mediafilter2

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var register: TextView
    private lateinit var login:Button
    private lateinit var forgotPass :TextView
    //private lateinit var etusername:EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        //mAuth = Firebase.getInstance()
        emailEt = findViewById(R.id.email)
        passwordEt = findViewById(R.id.password)
        register = findViewById((R.id.register))
        login = findViewById(R.id.login)
        forgotPass=findViewById(R.id.forgotPassword)
        progressBar=findViewById(R.id.progressBar)
        //etusername= findViewById<EditText>(R.id.et_username)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun onClick(view: View){

        when(view.id){
            R.id.login ->{
                progressBar.setVisibility(View.VISIBLE)
                login()

            }

            R.id.register ->{
                val intent = Intent(this,SignUpActivity::class.java)
                startActivity(intent)

            }
            R.id.forgotPassword ->{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Forgot Password")
                val view: View = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
                val username = view.findViewById<EditText>(R.id.et_username)
                builder.setView(view)
                builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _->
                        forgotPassword(username)
                })
                builder.setNegativeButton("close", DialogInterface.OnClickListener { _, _->

                })
                builder.show()

            }
        }

    }

    fun login(){
        var email = emailEt.text.toString()
        var password = passwordEt.text.toString()

        if(TextUtils.isEmpty(email)){
            emailEt.setError("Enter your email")
            progressBar.setVisibility(View.GONE)
            return
        }
        else  if(TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.GONE)
            passwordEt.setError("Enter your passsword")
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
                progressBar.setVisibility(View.GONE)
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
                progressBar.setVisibility(View.GONE)
            }
        })
    }

    fun forgotPassword(username:EditText){
            var email = username.text.toString()
        if(TextUtils.isEmpty(email)){
            username.setError("Enter your email")
            return
        }

        else if(!isValidEmail(email)){
            username.setError("Please enter a valid email")
            return
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this,"Email sent", Toast.LENGTH_SHORT ).show()
                }
            }


    }
    private fun isValidEmail(target:CharSequence):Boolean{
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
    }
}