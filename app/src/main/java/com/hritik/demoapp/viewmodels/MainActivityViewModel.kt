package com.hritik.demoapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hritik.demoapp.models.User
import com.hritik.demoapp.ui.activities.MainActivity
import com.hritik.demoapp.utils.ValidationUtils
import java.util.concurrent.TimeUnit

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val uiState = MutableLiveData<Int>()
    val messageToast = MutableLiveData<String>()
    var storedVerificationId = ""
    lateinit var user: User
    private val usersRef = FirebaseDatabase.getInstance().reference.child("users")

    private var numberListener: ValueEventListener? = null

    private val auth = FirebaseAuth.getInstance()


    fun validateNumber(number: String, activity: MainActivity) {
        uiState.value = MainActivity.STATE_REQUEST_OTP_PROCESSING
        if (!ValidationUtils.isValidPhoneNumber(number)) {
            uiState.value = MainActivity.STATE_INVALID_NUMBER
            return
        }
        checkPhoneNumberExists(number.replace("+91", ""), activity)
    }

    fun verifyOtp(mOtp: String, activity: MainActivity) {
        if (!ValidationUtils.isValidOtp(mOtp)) {
            uiState.value = MainActivity.STATE_OTP_VERIFIED_FAILURE
            return
        }
        uiState.value = MainActivity.STATE_SUBMIT_OTP_PROCESSING
        signInWithPhoneAuthCredential(
            PhoneAuthProvider.getCredential(storedVerificationId, mOtp),
            activity
        )
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        activity: MainActivity
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    uiState.value = MainActivity.STATE_OTP_VERIFIED
                } else {
                    uiState.value = MainActivity.STATE_OTP_VERIFIED_FAILURE
                }
                Log.e(TAG, "signInWithCredential:${task.isSuccessful}", task.exception)
                Log.e(TAG, "Otp${credential.smsCode}")
            }.addOnFailureListener {
                it.printStackTrace()
                uiState.value = MainActivity.NETWORK_ERROR
            }
    }

    private fun checkPhoneNumberExists(number: String, activity: MainActivity): Boolean {
        if (numberListener != null) {
            usersRef.child(number).removeEventListener(numberListener!!)
        }
        numberListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User::class.java)!!
                    requestOtp(number, activity)
                } else {
                    uiState.value = MainActivity.STATE_INVALID_NUMBER
                }
            }

        }
        usersRef.child(number).addListenerForSingleValueEvent(numberListener!!)
        return false
    }

    fun requestOtp(number: String, activity: MainActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$number", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            activity, // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    uiState.value = MainActivity.STATE_OTP_VERIFIED
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    messageToast.value = exception.message ?: "No Message"
                    uiState.value = MainActivity.STATE_OTP_VERIFIED_FAILURE
                    exception.printStackTrace()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    uiState.value = MainActivity.STATE_CODE_SENT
                    storedVerificationId = verificationId
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    uiState.value = MainActivity.NETWORK_ERROR
                }
            })
    }

    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }
}