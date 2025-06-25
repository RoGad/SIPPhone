package com.example.phoneapp.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.CallStatus

@Composable
fun MainScreen(
    callState: CallState?,
    onCall: () -> Unit,
    onHangup: () -> Unit,
    answerCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SIP Call Demo",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (callState != null) {
            Text("Status: ${callState.status}")
            Text("Remote: ${callState.remoteAddress ?: "-"}")
            Text("Active: ${callState.isActive}")
            Text("Duration: ${callState.duration}s", modifier = Modifier.padding(bottom = 24.dp))
        } else {
            Text("No call yet", modifier = Modifier.padding(bottom = 24.dp))
        }

        Button(
            onClick = onCall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("Call")
        }

        Button(
            onClick = onHangup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("Hang Up")
        }

        Button(
            onClick = answerCall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("Answer Call")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview(){
    MainScreen(
        callState = CallState(
            status = CallStatus.CONNECTED,
            remoteAddress = "sip:echo@sip.linphone.org",
            isActive = true,
            duration = 42
        ),
        onCall = {},
        onHangup = {},
        answerCall = {}
    )
}
