package sonu.finds.secureyourself.services

import android.telecom.Call
import android.telecom.InCallService
import sonu.finds.secureyourself.activity.DialerActivity
import sonu.finds.secureyourself.utills.OngoingCall

class CallService : InCallService() {

    override fun onCallAdded(call: Call) {
        OngoingCall.call = call
        DialerActivity.start(this, call)
    }

    override fun onCallRemoved(call: Call) {
        OngoingCall.call = null
    }
}