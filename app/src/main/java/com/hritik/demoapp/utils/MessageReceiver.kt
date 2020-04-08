package com.hritik.demoapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage


class MessageReceiver(private val smsHandler: SmsHandler) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        /* Retrieve the sms message chunks from the intent */
        val rawSmsChunks: Array<SmsMessage> = try {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
        } catch (nlp: NullPointerException) {
            nlp.printStackTrace()
            return
        }
        var message = ""
        for (rawSmsChunk in rawSmsChunks) {
            val smsChunk: String = rawSmsChunk.displayMessageBody
            if (smsChunk.substring(0, 6).toIntOrNull() != null) {
                message = smsChunk.substring(0, 6)
            }
        }
        if (message.isNotBlank()) {
            smsHandler.handleSms(message)
        }
    }
}