package com.example.siptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;

public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SipAudioCall incomingCall = null;
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            MainActivity mainActivity = (MainActivity) context;
            incomingCall = mainActivity.sipData.sipManager.takeAudioCall(intent, listener);
            mainActivity.sipData.sipAudioCall = incomingCall;
        } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }

        Intent callInActivity;
        callInActivity = new Intent(context, CallingInActivity.class);
        context.startActivity(callInActivity);
    }
}
