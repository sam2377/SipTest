package com.example.siptest;

import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallingInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button answer;
    private Button hang_up;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callingin);
        answer = findViewById(R.id.button_answer_callingIn);
        hang_up = findViewById(R.id.button_hangup_callingIn);
        textView = findViewById(R.id.textview_callingIn);

        textView.setText(MainActivity.sipData.sipAudioCall.getPeerProfile().getDisplayName() + " is calling you");
        answer.setOnClickListener(this);
        hang_up.setOnClickListener(this);

        SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
                finish();
            }
        };
        MainActivity.sipData.sipAudioCall.setListener(listener);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_answer_callingIn:
                if(!MainActivity.sipData.sipAudioCall.isInCall()){
                    try {
                        MainActivity.sipData.sipAudioCall.answerCall(30);
                        MainActivity.sipData.sipAudioCall.startAudio();
                        MainActivity.sipData.sipAudioCall.setSpeakerMode(true);
                        if(MainActivity.sipData.sipAudioCall.isMuted()) {
                            MainActivity.sipData.sipAudioCall.toggleMute();
                        }

                    }catch(Exception e){
                        Log.d("SIP",e.getMessage());
                    }
                    textView.setText("Call Established");
                }
                break;
            case R.id.button_hangup_callingIn:
                try {
                    MainActivity.sipData.sipAudioCall.endCall();
                } catch (Exception e) {
                    Log.d("SIP", e.getMessage());
                }
                break;
        }
    }

}
