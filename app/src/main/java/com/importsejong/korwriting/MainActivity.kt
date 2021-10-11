package com.importsejong.korwriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setTitle
        val title = findViewById<TextView>(R.id.title)
        title.text = resources.getString(R.string.app_name)
    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.btn_takePhoto -> {
                val intent = Intent(this, PhotoActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_bookmark -> {
                val intent = Intent(this, BookmarkActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_information -> {
                val intent = Intent(this, InformationActivity::class.java)
                startActivity(intent)
            }
        }
    }
}