package com.hritik.demoapp

import com.hritik.demoapp.utils.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoginUnitTests {
    @Test
    fun phoneNumber_CorrectSampleWithPlus_ReturnsTrue() {
        val result = ValidationUtils.isValidPhoneNumber("+917988016457")
        assertTrue(result)
    }

    @Test
    fun phoneNumber_CorrectSampleNormal_ReturnsTrue() {
        val result = ValidationUtils.isValidPhoneNumber("7988016457")
        assertTrue(result)
    }

    @Test
    fun phoneNumber_InCorrectSampleStartChar_ReturnsFalse() {
        val result = ValidationUtils.isValidPhoneNumber("1950327599")
        assertFalse(result)
    }

    @Test
    fun phoneNumber_InCorrectSampleEmptyString_ReturnsFalse() {
        val result = ValidationUtils.isValidPhoneNumber("")
        assertFalse(result)
    }

    @Test
    fun phoneNumber_InCorrectDifferentCountryCode_ReturnsFalse() {
        val result = ValidationUtils.isValidPhoneNumber("+921950327599")
        assertFalse(result)
    }

    @Test
    fun phoneNumber_InCorrectSampleShortNumber_ReturnsFalse() {
        val result = ValidationUtils.isValidPhoneNumber("7234599")
        assertFalse(result)
    }

    @Test
    fun phoneNumber_InCorrectSampleNonNumeric_ReturnsFalse() {
        val result = ValidationUtils.isValidPhoneNumber("7aae234599")
        assertFalse(result)
    }

    @Test
    fun otp_CorrectSample_ReturnsTrue() {
        val result = ValidationUtils.isValidOtp("116457")
        assertTrue(result)
    }

    @Test
    fun otp_CorrectSampleLeadingZero_ReturnsTrue() {
        val result = ValidationUtils.isValidOtp("016457")
        assertTrue(result)
    }

    @Test
    fun otp_InCorrectSampleShortLength_ReturnsFalse() {
        val result = ValidationUtils.isValidOtp("7599")
        assertFalse(result)
    }

    @Test
    fun otp_InCorrectSampleNonNumeric_ReturnsFalse() {
        val result = ValidationUtils.isValidOtp("3S7599")
        assertFalse(result)
    }

    @Test
    fun otp_InCorrectSampleEmptyString_ReturnsFalse() {
        val result = ValidationUtils.isValidOtp("")
        assertFalse(result)
    }
}
