package com.importsejong.korwriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        //setTitle
        val toolbar = findViewById<TextView>(R.id.title)
        toolbar.text = resources.getString(R.string.information)
        val btn_back = findViewById<ImageView>(R.id.btn_back)
        btn_back.setOnClickListener{onClick(btn_back)}
    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.btn_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}