package com.example.phoneapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.phoneapp.data.CallState
import com.example.phoneapp.ui.components.MainScreen

@Composable
fun App(
    callState: CallState?,
    onCall: () -> Unit,
    onHangup: () -> Unit,
    answerCall: () -> Unit,
){
    MaterialTheme {
        MainScreen(
            callState = callState,
            onCall = onCall,
            onHangup = onHangup,
            answerCall = answerCall
        )

    }
}