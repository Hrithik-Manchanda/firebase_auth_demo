package com.hritik.demoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hritik.demoapp.utils.PreferenceUtility

abstract class BaseActivity : AppCompatActivity() {
    lateinit var preferenceUtility: PreferenceUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceUtility = PreferenceUtility(this)
    }

    fun openActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        finish()
    }
}