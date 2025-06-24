package com.example.phoneapp.android.sipclient

import android.content.Context
import com.example.phoneapp.sipclient.SipClient

class AndroidSipClient(private val context: Context): SipClient {
    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun createAccount(
        username: String,
        password: String,
        domain: String
    ) {
        TODO("Not yet implemented")
    }

    override fun register() {
        TODO("Not yet implemented")
    }

    override fun makeCall(address: String) {
        TODO("Not yet implemented")
    }

    override fun answerCall() {
        TODO("Not yet implemented")
    }

    override fun hangup() {
        TODO("Not yet implemented")
    }

}