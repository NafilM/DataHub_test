package com.example.datahubtest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.FileProvider

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_CAPTURE_IMAGE = 100
    private var imageCounter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv_qrdata: TextView = findViewById(R.id.tv_qrdata)
        val qrcodedata = intent.getStringExtra("qrCodeData")
        val captureButton: Button = findViewById(R.id.btn_capture)

        tv_qrdata.text = qrcodedata

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("qrcodedata", qrcodedata)
        editor.apply()

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAPTURE_IMAGE
                )
            } else {
                captureImage()
            }
        }
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val imageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyImages")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val qrData = sharedPrefs.getString("qrcodedata", null)
            val imageFile = File(imageDir, qrData+"-$imageCounter.jpg")
            imageCounter++

            val imageUri = FileProvider.getUriForFile(
                this,
                "com.example.datahubtest.fileprovider",
                imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Image captured successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

}