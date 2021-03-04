package com.example.mediafilter2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.mlkit.vision.text.TextRecognition

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }
    fun textRecognition(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }
    fun faceDetection(view: View){
        startActivity(Intent(this, SignOutActivity::class.java))
    }
    fun barcodeScanner(view: View){
        startActivity(Intent(this, BarcodeScannerActivity::class.java))
    }
    fun imageLabeling(view: View){
        startActivity(Intent(this, MediaFilterActivity::class.java))
    }
}