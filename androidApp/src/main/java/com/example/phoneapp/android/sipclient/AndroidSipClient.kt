// androidApp/src/main/java/com/example/phoneapp/android/sipclient/AndroidSipClient.kt
package com.example.phoneapp.android.sipclient

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.phoneapp.android.constant.Constants
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
        private lateinit var core: Core
        private var listener: ((CallState) -> Unit)? = null

        private val handler = Handler(Looper.getMainLooper())
        private val ticker = object : Runnable {
            override fun run() {
                core.iterate()
                handler.postDelayed(this, Constants.DELAY_MILLIS.toLong())
            }
        }

        override fun initialize() {
            val factory = Factory.instance()
            val configPath = context.filesDir.resolve(Constants.LINPHONERC).absolutePath

            core = factory.createCore(configPath, null, context)
            core.addListener(object : CoreListenerStub() {
                override fun onAccountRegistrationStateChanged(
                    core: Core,
                    account: Account,
                    state: RegistrationState?,
                    message: String
                ) {
                    super.onAccountRegistrationStateChanged(core, account, state, message)
                }

                override fun onCallStateChanged(
                    core: Core,
                    call: Call,
                    state: Call.State?,
                    message: String
                ) {
                    val status = when (state){
                        Call.State.OutgoingInit -> CallStatus.OUTGOING
                        Call.State.Idle -> CallStatus.IDLE
                        Call.State.IncomingReceived -> CallStatus.INCOMING
                        Call.State.OutgoingProgress -> CallStatus.OUTGOING
                        Call.State.OutgoingRinging -> CallStatus.OUTGOING
                        Call.State.OutgoingEarlyMedia -> CallStatus.OUTGOING
                        Call.State.Connected -> CallStatus.CONNECTED
                        Call.State.Pausing -> CallStatus.PAUSED
                        Call.State.Paused -> CallStatus.PAUSED
                        Call.State.StreamsRunning -> CallStatus.CONNECTED
                        Call.State.Error -> CallStatus.ERROR
                        Call.State.IncomingEarlyMedia -> CallStatus.CONNECTED
                        Call.State.PushIncomingReceived -> TODO()
                        Call.State.Resuming -> TODO()
                        Call.State.Referred -> TODO()
                        Call.State.End -> TODO()
                        Call.State.PausedByRemote -> TODO()
                        Call.State.UpdatedByRemote -> TODO()
                        Call.State.Updating -> TODO()
                        Call.State.Released -> TODO()
                        Call.State.EarlyUpdatedByRemote -> TODO()
                        Call.State.EarlyUpdating -> TODO()
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
            } )
            core.start()
            handler.post(ticker)

            Log.d("AndroidSipClient", "Core initialized")
        }

        override fun createAccount(account: SipAccount) {
            val factory = Factory.instance()

            val identity = factory.createAddress("sip:${account.username}@${account.domain}")
            val authInfo = Factory.instance().createAuthInfo(
                account.username,
                null,
                account.password,
                null,
                null,
                account.domain
            )
            core.addAuthInfo(authInfo)

            val accountParams = core.createAccountParams().apply {
                identityAddress = identity
                serverAddress = factory.createAddress("sip:${account.domain}")
                isRegisterEnabled = true
            }

            this.account = core.createAccount(accountParams).also {
                core.addAccount(it)
                core.defaultAccount = it
            }
        }

    override fun register() {
        account?.let {
            it.params.isRegisterEnabled = true
            core.addAccount(it)
            core.defaultAccount = it
        }
    }

    override fun makeCall(address: String) {
        val addr = core.interpretUrl(address, true)
        if (addr != null) {
            core.inviteAddress(addr)
            Log.d("AndroidSipClient", "Calling: $address")
        } else {
            Log.e("AndroidSipClient", "Bad SIP address: $address")
        }
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
        val account = core.defaultAccount
        account ?: return

        val params = account.params
        val clonedParams = params.clone()

        clonedParams.isRegisterEnabled = false
        account.params = clonedParams

    }

    override fun delete() {
        val account = core.defaultAccount
        account ?: return
        core.removeAccount(account)

        core.clearAccounts()

        core.clearAllAuthInfo()
    }
}
