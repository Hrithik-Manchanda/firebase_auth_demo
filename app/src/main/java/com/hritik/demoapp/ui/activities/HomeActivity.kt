package com.hritik.demoapp.ui.activities

import android.os.Bundle
import com.hritik.demoapp.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initView()
    }

    private fun initView() {
        name.text = preferenceUtility.name
        logout.setOnClickListener {
            preferenceUtility.logout()
            openActivity(MainActivity::class.java)
        }
    }
}
