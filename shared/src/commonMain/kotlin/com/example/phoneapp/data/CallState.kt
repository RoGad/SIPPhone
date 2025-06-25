package com.example.phoneapp.data

data class CallState(
    val isActive: Boolean,
    val remoteAddress: String?,
    val duration: Int,
    val status: CallStatus
)