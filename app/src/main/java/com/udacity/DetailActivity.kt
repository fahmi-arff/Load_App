package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private val fileName by lazy { intent.getStringExtra("fileName").toString() }
    private val status by lazy { intent.getStringExtra("status").toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        cancelNotifications(this)

        file_name.text = fileName
        if(status == "Fail") status_value.setTextColor(Color.RED)
        status_value.text   = status

        ok_button.setOnClickListener {
            returnToMainActivity()
        }
    }

    private fun returnToMainActivity() {
        val  main = Intent(this, MainActivity::class.java)
        startActivity(main)
    }

}
