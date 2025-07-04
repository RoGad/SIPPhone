package com.example.phoneapp.ui.components

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
import androidx.compose.ui.unit.dp
import com.example.phoneapp.data.CallState
import com.example.phoneapp.generated.resources.Res
import com.example.phoneapp.generated.resources.answer_call
import com.example.phoneapp.generated.resources.call
import com.example.phoneapp.generated.resources.demo_call
import com.example.phoneapp.generated.resources.hang_up
import org.jetbrains.compose.resources.stringResource

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
            text = stringResource(Res.string.demo_call),
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
            Text( text = stringResource(Res.string.call) )
        }

        Button(
            onClick = onHangup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text( text = stringResource(Res.string.hang_up) )
        }

        Button(
            onClick = answerCall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text( text = stringResource(Res.string.answer_call) )
        }
    }
}
