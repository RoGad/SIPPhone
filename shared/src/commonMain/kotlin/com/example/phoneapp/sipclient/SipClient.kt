package com.example.phoneapp.sipclient

interface SipClient {
    fun initialize()
    fun createAccount(username: String, password: String, domain: String)
    fun register()
    fun makeCall(address: String)
    fun answerCall()
    fun hangup()
}