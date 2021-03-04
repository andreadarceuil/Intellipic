package com.example.mediafilter2

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var signIn: TextView
    private lateinit var progressBar:ProgressBar
    private lateinit var registerButton: Button
    private lateinit var forgotPass : TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var confirm_passwordEt: EditText
    private lateinit var banner:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signIn = findViewById(R.id.signIn)
        registerButton = findViewById(R.id.registerUser)
        forgotPass = findViewById(R.id.forgotPassword1)
        emailEt = findViewById(R.id.email2)
        passwordEt = findViewById(R.id.password2)
        confirm_passwordEt = findViewById(R.id.password3)
        banner = findViewById(R.id.banner)
        progressBar=findViewById(R.id.progressBar2)
        firebaseAuth = FirebaseAuth.getInstance()
        //progressDialog = findViewById<ProgressDialog>(R.id.p)

    }

    fun onClick(view: View){

        when(view.id){
            R.id.signIn ->{
                val intent = Intent(this,SignInActivity::class.java)
                startActivity(intent)

            }
            R.id.forgotPassword1 ->{
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

            R.id.registerUser ->{
                register()

            }
            R.id.banner ->{
                val intent = Intent(this,SignInActivity::class.java)
                startActivity(intent)

            }
        }

    }

    fun register(){
        var email = emailEt.text.toString()
        var password = passwordEt.text.toString()
        var confirmPassword = confirm_passwordEt.text.toString()

      if(TextUtils.isEmpty(email)){
          emailEt.setError("Enter your email")
          return
      }
        else  if(TextUtils.isEmpty(password)){
          passwordEt.setError("Enter your passsword")
          return
      } else  if(TextUtils.isEmpty(confirmPassword)){
          confirm_passwordEt.setError("Please Confirm your passsword")
          return
      }else if(!password.equals(confirmPassword)){
          confirm_passwordEt.setError("Passwords do not match")
          return
      }else if(password.length<4){
          passwordEt.setError("Passwords must be at least 4 characters")
          return
      }else if(!isValidEmail(email)){
          emailEt.setError("Please enter a valid email")
          return
      }
    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
        if(it.isSuccessful){
            Toast.makeText(this, "Successfully registered", Toast.LENGTH_LONG).show()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Sign up failed!", Toast.LENGTH_LONG).show()
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