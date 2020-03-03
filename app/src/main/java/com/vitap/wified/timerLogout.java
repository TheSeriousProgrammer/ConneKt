package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class timerLogout extends AppCompatActivity {
    TimePicker timePicker;
    Calendar cal;
    TextView status ;
    String registration_no;
    Context parent = this ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_logout);
        Bundle bundle = getIntent().getExtras();

        registration_no = bundle.getString("regNo");
        timePicker = findViewById(R.id.simpleTimePicker);
        cal = Calendar.getInstance();
        status = findViewById(R.id.status);

        commons.initiateBackgroundRunPermission(parent);

    }

    public void activateTimer(View view){

        final int diff = timePicker.getHour()*60*60+timePicker.getMinute()*60 - (cal.get(Calendar.HOUR_OF_DAY)*60*60+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND));

        if(diff/30>=1){
            status.setText("Timer Activated");
            commons.sendNotification(parent,"Timer Logout activated",
                    String.format("Logout request will be sent at %2d:%2d",timePicker.getHour(),timePicker.getMinute()));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{Thread.sleep(diff * 1000);}catch(InterruptedException e){;}
                    requests req = new requests();
                    int i =3 ;
                    while(i>0){
                        if(req.logoutRequest(registration_no)){
                            commons.sendNotification(parent,"Wifi Logout Request Sent","Timed Logout Request Sent. Tap to login again",0);
                            SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("status",false);
                            editor.commit();

                            break;
                        }
                        try{Thread.sleep(10);}catch(InterruptedException e){;}
                        i--;
                    }
                    if(i==0){
                        commons.sendNotification(parent,"Failed Wifi Logout Request","Unable to send logout request scheduled in timerlogout\nServer not reachable",0);
                    }
                }
            }).start();
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            status.setText("Please Set a time later than current time");
        }
    }

}
