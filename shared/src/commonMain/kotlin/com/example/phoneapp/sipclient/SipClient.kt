// commonMain/src/commonMain/kotlin/com/example/phoneapp/sipclient/SipClient.kt
package com.example.phoneapp.sipclient

import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.SipAccount

interface SipClient {
    fun initialize()
    fun createAccount(account: SipAccount)
    fun register()
    fun makeCall(address: String)
    fun answerCall()
    fun hangup()
    fun setCallStateListener(listener: (CallState) -> Unit)
}
