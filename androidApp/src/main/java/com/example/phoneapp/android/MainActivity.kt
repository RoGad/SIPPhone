package com.example.phoneapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.phoneapp.android.sipclient.AndroidSipClient
import com.example.phoneapp.android.ui.MainScreen
import com.example.phoneapp.android.ui.theme.MyApplicationTheme
import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.SipAccount

class MainActivity : ComponentActivity() {

    private lateinit var sipClient: AndroidSipClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callState = mutableStateOf<CallState?>(null)

        sipClient = AndroidSipClient(this).apply {
            initialize()
            createAccount(SipAccount("", "", ""))
            setCallStateListener { state ->
                callState.value = state
            }
            register()
        }

        setContent {
            MyApplicationTheme {
                MainScreen(
                    callState = callState.value,
                    onCall = { sipClient.makeCall("sip:echo@sip.linphone.org") },
                    onHangUp = { sipClient.hangup() }
                )
            }
        }
    }
}

