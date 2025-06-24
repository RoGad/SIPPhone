package com.example.phoneapp.data

data class SipAccount(
    val username: String,
    val password: String,
    val domain: String,
    val proxy: String? = null
)
