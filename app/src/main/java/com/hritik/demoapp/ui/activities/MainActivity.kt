package com.hritik.demoapp.ui.activities

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hritik.demoapp.R
import com.hritik.demoapp.utils.MessageReceiver
import com.hritik.demoapp.utils.NetworkUtils
import com.hritik.demoapp.utils.SmsHandler
import com.hritik.demoapp.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), SmsHandler {
    companion object {
        const val REQUEST_SMS_RECEIVE_PERMISSION_CODE = 101
        const val STATE_REQUEST_OTP = 0
        const val STATE_CODE_SENT = 1
        const val STATE_REQUEST_OTP_PROCESSING = 2
        const val STATE_SUBMIT_OTP_PROCESSING = 3
        const val STATE_OTP_VERIFIED = 4
        const val STATE_OTP_VERIFIED_FAILURE = 5
        const val STATE_INVALID_NUMBER = 6
        const val NETWORK_ERROR: Int = 7
    }

    private val receiver = MessageReceiver(this)

    private var currentState = -100

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkIsLoggedIn()
        updateUIwithState(STATE_REQUEST_OTP)
        initViewModelAndListeners()
        initClickListeners()
    }

    private fun checkIsLoggedIn() {
        if (preferenceUtility.isLoggedIn) {
            openActivity(HomeActivity::class.java)
        }
    }

    private fun initViewModelAndListeners() {
        mainActivityViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(MainActivityViewModel::class.java)
        mainActivityViewModel.uiState.observe(this, Observer {
            updateUIwithState(it)
        })
        mainActivityViewModel.messageToast.observe(this, Observer {
            showToast(it)
        })
    }

    private fun initClickListeners() {
        submitBtn.setOnClickListener {
            if (NetworkUtils.isInternetAvailable(this)) {
                if (currentState == STATE_REQUEST_OTP || currentState == STATE_INVALID_NUMBER) {
                    requestSmsPermission()
                    val phoneNumber = phone.text?.toString() ?: ""
                    mainActivityViewModel.validateNumber(phoneNumber, this)
                } else {
                    mainActivityViewModel.verifyOtp(otp.text?.toString() ?: "", this)
                }
            } else {
                updateUIwithState(NETWORK_ERROR)
            }
        }
        changeNum.setOnClickListener {
            updateUIwithState(STATE_REQUEST_OTP)
        }
    }

    private fun updateUIwithState(state: Int) {
        // In case when OnVerificationFailed gets called without code sent
        if (state == STATE_OTP_VERIFIED_FAILURE && (currentState == STATE_REQUEST_OTP || currentState == STATE_REQUEST_OTP_PROCESSING)) {
            return
        }
        when (state) {
            STATE_REQUEST_OTP -> {
                submitBtn.text = getString(R.string.request_otp)
                changeNum.visibility = View.INVISIBLE
                phone.setText("")
                otp.setText("")
                phone.isEnabled = true
                otp.isEnabled = false
                submitBtn.isEnabled = true
                phone.requestFocus()
            }
            STATE_REQUEST_OTP_PROCESSING -> {
                submitBtn.isEnabled = false
                submitBtn.text = getString(R.string.requesting)
                changeNum.visibility = View.VISIBLE
                phone.isEnabled = false
            }
            STATE_CODE_SENT -> {
                submitBtn.text = getString(R.string.submit_otp)
                changeNum.visibility = View.VISIBLE
                submitBtn.isEnabled = true
                otp.isEnabled = true
            }
            STATE_SUBMIT_OTP_PROCESSING -> {
                submitBtn.isEnabled = false
                submitBtn.text = getString(R.string.processing)
                changeNum.visibility = View.VISIBLE
                phone.isEnabled = false
                otp.isEnabled = false
            }
            STATE_OTP_VERIFIED_FAILURE -> {
                showToast(getString(R.string.invalid_otp))
                submitBtn.isEnabled = true
                submitBtn.text = getString(R.string.submit_otp)
                otp.setText("")
                otp.isEnabled = true
                otp.requestFocus()
            }
            STATE_OTP_VERIFIED -> {
                showToast(getString(R.string.otp_verified))
                preferenceUtility.name =
                    mainActivityViewModel.user.name ?: getString(R.string.app_name)
                preferenceUtility.isLoggedIn = true
                openActivity(HomeActivity::class.java)
            }
            STATE_INVALID_NUMBER -> {
                showToast(getString(R.string.invalid_number))
                updateUIwithState(STATE_REQUEST_OTP)
            }
            NETWORK_ERROR -> {
                showToast("Network Error!! Check your connection")
                if (currentState == STATE_REQUEST_OTP_PROCESSING) {
                    updateUIwithState(STATE_REQUEST_OTP)
                } else if (currentState == STATE_SUBMIT_OTP_PROCESSING) {
                    updateUIwithState(STATE_CODE_SENT)
                }
            }
        }
        currentState = state
    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissionList = arrayOf(permission)
            ActivityCompat.requestPermissions(
                this,
                permissionList,
                REQUEST_SMS_RECEIVE_PERMISSION_CODE
            )
        } else {
            registerMessageReceiver()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == REQUEST_SMS_RECEIVE_PERMISSION_CODE) {
            registerMessageReceiver()
        } else {
            showToast("Permission Denied")
        }
    }

    override fun onResume() {
        super.onResume()
        requestSmsPermission()
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(receiver)
        } catch (ignored: Exception) {
        }
    }

    private fun registerMessageReceiver() {
        try {
            unregisterReceiver(receiver)
        } catch (ignored: Exception) {
        }
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(receiver, filter)
        Log.d("Registered", "MessageReceiver")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun handleSms(message: String) {
        otp.setText(message)
        submitBtn.callOnClick()
    }
}
