package com.example.phoneapp.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.example.phoneapp.android.sipclient.AndroidSipClient
import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.SipAccount
import com.example.phoneapp.ui.App
import com.example.phoneapp.android.ui.theme.MyApplicationTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private var sipClient: AndroidSipClient? = null
    private val callState = mutableStateOf<CallState?>(null)
    private var fcmToken: String? = null

    private val requestMic = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            initializeSip()
        } else {
            Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPush = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(this, "Push notifications permission is required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                App(
                    callState = callState.value,
                    onCall = { makeCall() },
                    onHangup = { hangupCall() },
                    answerCall = { answerCall() }
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPush.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            fcmToken = task.result
            Log.d("MainActivity", "FCM Token: $fcmToken")

            val prefs = getSharedPreferences("push_prefs", MODE_PRIVATE)
            prefs.edit().putString("fcm_token", fcmToken).apply()
        }
        requestMic.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun initializeSip() {
        try {
            sipClient = AndroidSipClient(this).apply {
                initialize()

                val prefs = getSharedPreferences("push_prefs", MODE_PRIVATE)
                val savedToken = prefs.getString("fcm_token", null)
                val tokenToUse = fcmToken ?: savedToken ?: ""

                if (tokenToUse.isNotEmpty()) {
                    createAccount(
                        SipAccount("testrogadb", "demo0R", "sip.linphone.org"),
                        tokenToUse
                    )
                    setCallStateListener { state ->
                        callState.value = state
                        Log.d("MainActivity", "Call state updated: $state")
                    }
                    register()
                    Log.d("MainActivity", "SIP client initialized successfully")
                } else {
                    Log.w("MainActivity", "No FCM token available yet")
                    Toast.makeText(this@MainActivity, "Waiting for FCM token...", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing SIP client", e)
            Toast.makeText(this, "Error initializing SIP: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun makeCall() {
        sipClient?.let { client ->
            client.makeCall("sip:rogadtest@sip.linphone.org")
            Log.d("MainActivity", "Making call")
        } ?: run {
            Log.w("MainActivity", "SIP client not initialized")
            Toast.makeText(this, "SIP not initialized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hangupCall() {
        sipClient?.let { client ->
            client.hangup()
            Log.d("MainActivity", "Hanging up call")
        }
    }

    private fun answerCall() {
        sipClient?.let { client ->
            client.answerCall()
            Log.d("MainActivity", "Answering call")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sipClient?.unregister()
    }
}

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
    MyApplicationTheme {
        App(
            callState = null,
            onCall = {},
            onHangup = {},
            answerCall = {}
        )
    }
}