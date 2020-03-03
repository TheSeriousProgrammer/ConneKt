package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class smartLogout extends AppCompatActivity {
    WifiManager wifiManager ;
    WifiInfo wifiInfo ;
    String registration_no ;
    Context parent = this ;
    NotificationManager mNotificationManager ;
    SeekBar seekBar;
    TextView description;
    TextView description2;
    SharedPreferences sharedPref;
    int percentage ;
    final String desc  = "The feature works by constantly monitoring your wifi signal strength\n\nIf the signal strengh drops to ";
    final String desc_part2 =", a logout request will be sent from your id to prevent your ID's lock out\n\nThe feature assumes that you are leaving the network when the signal strength drops below the threshold";
    final String desc2 = "Current threshold : ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_logout);

        description  = findViewById(R.id.textView7);
        description2 = findViewById(R.id.textView9);

        sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
        percentage = sharedPref.getInt("minVal", 5);
        description.setText(desc+(((int)(percentage*10))+"%")+desc_part2);
        description2.setText(desc2+(((int)(percentage*10))+"%"));

        commons.initiateBackgroundRunPermission(parent);
        Bundle bundle = getIntent().getExtras();

        registration_no = bundle.getString("regNo");
        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress( (int) percentage ,true );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentage = progress;
                description.setText(desc+(((int)(percentage*10))+"%")+desc_part2);
                description2.setText(desc2+(((int)(percentage*10))+"%"));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public int giveWifiSignalStrength(){
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        int num = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 11);
        System.out.println(num);
        return num;
    }

    public void activate(View view){
        commons.sendNotification(parent,"Smart Logout Active","SmartLogout activated upon request");
        new Thread(
                new Runnable() {
                    requests req = new requests();
                    SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    @Override
                    public void run() {
                        editor.putInt("minVal",percentage);
                        editor.commit();
                        while(true){
                            if(giveWifiSignalStrength()<((int)percentage)){
                                boolean success = false;
                                for(int i=0;i<5;i++) { //5 retries

                                    if (req.logoutRequest(registration_no)) {
                                        commons.sendNotification(parent, "Logout Request Sent", "SmartLogout sent logout request triggered by low wifi signal", 0);

                                        editor.commit();
                                        success = true;
                                        break;
                                    }
                                }
                                if(!success){
                                    commons.sendNotification(parent,"Unable to send logout request","SmartLogout is unable to communicate to portal",0);
                                }
                                break;
                            }
                            try{Thread.sleep(50);}catch(InterruptedException e){;}
                        }

                    }
                }
        ).start();
        startActivity(new Intent(this,MainActivity.class));
    }


}
