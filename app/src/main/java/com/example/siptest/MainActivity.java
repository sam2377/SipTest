package com.example.siptest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.sip.SipRegistrationListener;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler handler;
    private Runnable runnable;

    public IncomingCallReceiver callReceiver;
    public static SipData sipData;

    private TextView textView;
    private Button button_register;
    private Button button_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.USE_SIP},0);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},0);
        }

        textView = findViewById(R.id.textview);
        button_register = findViewById(R.id.button_register);
        button_call = findViewById(R.id.button_call);
        button_register.setOnClickListener(this);
        button_call.setOnClickListener(this);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        };

        sipData = new SipData(this);
        sipData.setSipProfile();
        sipData.setPeer();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.siptest.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(callReceiver);
            sipData.sipManager.close(sipData.sipProfile.getUriString());
            Log.d("SIP","Quit");
        }catch (Exception e){
            textView.setText("Error");
        }
    }

    private void refresh(){
        textView.setText(sipData.getStatus());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                try{
                    Intent i = new Intent();
                    i.setAction("android.siptest.INCOMING_CALL");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, Intent.FILL_IN_DATA);
                    sipData.sipManager.open(sipData.sipProfile,pendingIntent,null);

                    sipData.sipManager.setRegistrationListener(sipData.sipProfile.getUriString(), new SipRegistrationListener() {
                        @Override
                        public void onRegistering(String localProfileUri) {
                            Log.d("SIP","Registering");
                            sipData.setStatus("Registering");
                            handler.post(runnable);
                        }

                        @Override
                        public void onRegistrationDone(String localProfileUri, long expiryTime) {
                            Log.d("SIP","Register Done");
                            sipData.setStatus("Register Done");
                            handler.post(runnable);
                        }
                        @Override
                        public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                            Log.d("SIP","Register Failed");
                            sipData.setStatus("Register Failed");
                            handler.post(runnable);
                        }
                    });
                }catch (Exception e){
                    textView.setText(e.getMessage());
                }
                break;
            case R.id.button_call:
                try {
                    if (sipData.sipManager.isRegistered(sipData.sipProfile.getUriString())) {
                        Intent callOutActivity;
                        callOutActivity = new Intent(getApplicationContext(), CallingOutActivity.class);
                        startActivity(callOutActivity);
                    }
                } catch (Exception e){
                    Log.d("SIP", e.getMessage());
                }

//                final SipAudioCall.Listener listener = new SipAudioCall.Listener(){
//                    @Override
//                    public void onCallEstablished(SipAudioCall call) {
//                        super.onCallEstablished(call);
//                        call.startAudio();
//                        call.setSpeakerMode(true);
//                        if(call.isMuted()){
//                            call.toggleMute();
//                        }
//                        Log.d("SIP", "call established");
////                Toast.makeText(getApplicationContext(),"calling",Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    public void onCallEnded(SipAudioCall call) {
//                        super.onCallEnded(call);
//                        call.close();
//                        Log.d("SIP", "call ended");
////                Toast.makeText(getApplicationContext(),"call end",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onRinging(SipAudioCall call, SipProfile caller) {
//                        super.onRinging(call, caller);
//                        Log.d("SIP", "ringing");
//                    }
//                };
//                try {
//                    call = sipManager.makeAudioCall(sipProfile.getUriString(), peer.getUriString(), listener, 30);
//                }catch (Exception e){
//                    Log.d("SIP","call exception");
//                }
                break;
        }
    }
}
