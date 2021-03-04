package com.example.mediafilter2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }
    fun textRecognition(view: View){
        startActivity(Intent(this, TextRecognitionActivity::class.java))
    }
    fun faceDetection(view: View){
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun mediaFilter(view: View){
        startActivity(Intent(this, MediaFilterActivity::class.java))
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.logout)
        if (menuItem != null) {
            val textView = menuItem.actionView as TextView
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
       return super.onOptionsItemSelected(item)
    }

}