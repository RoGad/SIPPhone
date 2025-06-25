package com.example.phoneapp.android

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import com.example.phoneapp.android.sipclient.AndroidSipClient
import com.example.phoneapp.android.ui.MainScreen
import com.example.phoneapp.android.ui.theme.MyApplicationTheme
import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.SipAccount

class MainActivity : ComponentActivity() {

    private lateinit var sipClient: AndroidSipClient
    private val callState = mutableStateOf<CallState?>(null)

    private val requestMic = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startSip()
        } else {
            Toast.makeText(this, "Microphone is required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestMic.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun startSip() {
        sipClient = AndroidSipClient(this).apply {
            initialize()
            createAccount(SipAccount("testrogadb", "demo0R", "sip.linphone.org"))
            setCallStateListener { state ->
                callState.value = state
            }
            register()
        }

        setContent {
            MyApplicationTheme {
                MainScreen(
                    callState = callState.value,
                    onCall = { sipClient.makeCall("sip:rogadtest@sip.linphone.org") },
                    onHangup = { sipClient.hangup() },
                    answerCall = { sipClient.answerCall() }
                )
            }
        }
    }
}
