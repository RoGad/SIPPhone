package com.example.phoneapp.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.phoneapp.android.R
import com.example.phoneapp.data.CallState

@Composable
fun MainScreen(
    callState: CallState?,
    onCall: () -> Unit,
    onHangUp: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = stringResource(R.string.demo_text),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}