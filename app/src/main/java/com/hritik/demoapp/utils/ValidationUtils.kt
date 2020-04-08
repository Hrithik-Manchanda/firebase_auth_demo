package com.hritik.demoapp.utils

object ValidationUtils {

    fun isValidPhoneNumber(number: String): Boolean {
        val newNumber = number.replace("+91", "")
        if (newNumber.length != 10) {
            return false
        }
        println(newNumber)
        val startChar = newNumber[0]
        return startChar.toInt() in 54..57 && newNumber.toLongOrNull() != null
    }

    fun isValidOtp(otp: String): Boolean {
        println(otp)
        if (otp.length != 6) {
            return false
        }
        for (character in otp) {
            if (character.toInt() !in 48..57) {
                return false
            }
        }
        return true
    }
}