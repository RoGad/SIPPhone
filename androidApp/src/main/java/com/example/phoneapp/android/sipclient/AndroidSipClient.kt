package com.example.phoneapp.android.sipclient

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.phoneapp.android.constant.Constants
import com.example.phoneapp.android.pushservice.MyApplication
import com.example.phoneapp.data.CallState
import com.example.phoneapp.data.CallStatus
import com.example.phoneapp.data.SipAccount
import com.example.phoneapp.sipclient.SipClient
import kotlinx.coroutines.Runnable
import org.linphone.core.Account
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory
import org.linphone.core.RegistrationState

class AndroidSipClient(private val context: Context) : SipClient {

    private var account: Account? = null
    internal val core: Core = (context.applicationContext as MyApplication).core
    private var listener: ((CallState) -> Unit)? = null

    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            core.iterate()
            handler.postDelayed(this, Constants.DELAY_MILLIS.toLong())
        }
    }

    override fun initialize() {
        core.addListener(object : CoreListenerStub() {
            override fun onAccountRegistrationStateChanged(
                core: Core,
                account: Account,
                state: RegistrationState?,
                message: String
            ) {
                Log.d("AndroidSipClient", "Registration state: $state, message: $message")
            }

            override fun onCallStateChanged(
                core: Core,
                call: Call,
                state: Call.State?,
                message: String
            ) {
                val status = when (state) {
                    Call.State.IncomingReceived -> CallStatus.INCOMING
                    Call.State.OutgoingInit,
                    Call.State.OutgoingProgress,
                    Call.State.OutgoingRinging,
                    Call.State.OutgoingEarlyMedia -> CallStatus.OUTGOING
                    Call.State.Connected,
                    Call.State.StreamsRunning,
                    Call.State.IncomingEarlyMedia -> CallStatus.CONNECTED
                    Call.State.Pausing,
                    Call.State.Paused -> CallStatus.PAUSED
                    Call.State.Error -> CallStatus.ERROR
                    Call.State.Idle -> CallStatus.IDLE
                    else -> CallStatus.IDLE
                }
                val cs = CallState(
                    isActive = (state == Call.State.StreamsRunning),
                    remoteAddress = call.remoteAddress.asString(),
                    duration = call.duration,
                    status = status
                )
                listener?.invoke(cs)
                Log.d("AndroidSipClient", "CallState -> $cs")
            }
        })

        core.isPushNotificationEnabled = true

        if (!core.isPushNotificationAvailable) {
            Log.w("AndroidSipClient", "Push notifications не доступны")
        }

        handler.post(ticker)
        Log.d("AndroidSipClient", "Core initialized")
    }

    override fun createAccount(accountConfig: SipAccount, fcmToken: String) {
        val factory = Factory.instance()
        val identity = factory.createAddress("sip:${accountConfig.username}@${accountConfig.domain}")
        val authInfo = factory.createAuthInfo(
            accountConfig.username,
            null,
            accountConfig.password,
            null,
            null,
            accountConfig.domain
        )
        core.addAuthInfo(authInfo)

        val accountParams = core.createAccountParams().apply {
            identityAddress = identity
            serverAddress = factory.createAddress("sip:${accountConfig.domain}")
            isRegisterEnabled = true

            pushNotificationAllowed = true
            remotePushNotificationAllowed = true

            if (fcmToken.isNotBlank()) {
                val contactParams = mutableMapOf<String, String>()
                contactParams["app-id"] = "firebase"
                contactParams["pn-provider"] = "firebase"
                contactParams["pn-param"] = fcmToken
                contactParams["pn-prid"] = fcmToken

                contactUriParameters = contactParams.toString()

                Log.d("AndroidSipClient", "Push config установлен с токеном: $fcmToken")
            } else {
                Log.w("AndroidSipClient", "FCM token пустой")
            }
        }

        this.account = core.createAccount(accountParams).also {
            core.addAccount(it)
            core.defaultAccount = it
            Log.d("AndroidSipClient", "Аккаунт создан и установлен как основной")
        }
    }

    override fun register() {
        account?.let {
            it.params = it.params.apply { isRegisterEnabled = true }
            core.addAccount(it)
            core.defaultAccount = it
            Log.d("AndroidSipClient", "Account registered")
        }
    }

    override fun makeCall(address: String) {
        core.interpretUrl(address, true)?.let { addr ->
            core.inviteAddress(addr)
            Log.d("AndroidSipClient", "Calling: $address")
        } ?: Log.e("AndroidSipClient", "Bad SIP address: $address")
    }

    override fun answerCall() {
        core.currentCall?.accept()
    }

    override fun hangup() {
        core.currentCall?.terminate()
    }

    override fun setCallStateListener(listener: (CallState) -> Unit) {
        this.listener = listener
    }

    override fun unregister() {
        core.defaultAccount?.let { acct ->
            acct.params = acct.params.clone().apply { isRegisterEnabled = false }
            Log.d("AndroidSipClient", "Аккаунт не зареган")
        }
    }

    override fun delete() {
        core.defaultAccount?.let { acct ->
            core.removeAccount(acct)
            core.clearAccounts()
            core.clearAllAuthInfo()
            Log.d("AndroidSipClient", "Все учетные записи и информация об авторизации очищены")
        }
    }
}
