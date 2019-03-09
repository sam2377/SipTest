package com.example.siptest;

import android.content.Context;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;

public class SipData {
    private String status;
    public SipProfile sipProfile;
    public SipProfile peer;
    public SipAudioCall sipAudioCall;
    public SipManager sipManager;

    public SipData(Context context){
        if(sipManager == null){
            sipManager = SipManager.newInstance(context);
        }
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String s){
        status = s;
    }

    public void setSipProfile() {
        try {
            SipProfile.Builder builder = new SipProfile.Builder("bbb","10.1.1.6");
            builder.setPassword("87654321");
            sipProfile = builder.build();
        }catch (Exception e) {
            Log.d("SIP",e.getMessage());
        }
    }
    public void setPeer(){
        try {
            SipProfile.Builder builder = new SipProfile.Builder("200", "10.1.1.6");
            peer = builder.build();
        }catch (Exception e){
            Log.d("SIP",e.getMessage());
        }
    }
}
