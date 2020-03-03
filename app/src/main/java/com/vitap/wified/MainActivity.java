package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button login , signout, smartLogout, timerLogout ,updater;
    EditText registration_id ,password;
    Switch autoSave ;
    String regNo , pssKey ;
    Context parent ;
    SharedPreferences sharedPref;
    WifiManager wifiMgr;
    WifiInfo wifiInfo;
    TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Wifi manager
        wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiInfo = wifiMgr.getConnectionInfo();
        //textView.setText(""+wifiInfo.getBSSID());


        //Getting data from previous session
        sharedPref= getSharedPreferences("mySettings", MODE_PRIVATE);

        regNo  = sharedPref.getString("regNo", "");
        pssKey =  sharedPref.getString("pssKey", "");


        //Initializing widgets
        updater = findViewById(R.id.updateChecker);
        status = findViewById(R.id.textView3);
        login = findViewById(R.id.login) ;
        signout = findViewById(R.id.signout);
        smartLogout = findViewById(R.id.smartLogout);
        timerLogout = findViewById(R.id.timerLogout);

        registration_id = findViewById(R.id.regNo);
        password = findViewById(R.id.password);

        autoSave = findViewById(R.id.switcher);

        registration_id.setText(regNo);
        password.setText(pssKey);

        smartLogout.setVisibility(View.INVISIBLE);
        timerLogout.setVisibility(View.INVISIBLE);
        updater.setVisibility(View.INVISIBLE);

        if(sharedPref.getString("lastRegNo","").equals("")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("status",true);
            editor.apply();
        }

        commons.initiateBackgroundRunPermission(parent);
        monitor();
        turnOffMobileData();
    }

    public void login(View view){

        Intent loginPage = new Intent(this,login.class);
        loginPage.putExtra("regNo",registration_id.getText().toString());
        loginPage.putExtra("pssd",password.getText().toString());
        loginPage.putExtra("state",autoSave.isChecked());
        startActivity(loginPage);

    }

    public void logout(View view){

        Intent loginPage = new Intent(this,logout.class);
        loginPage.putExtra("regNo",registration_id.getText().toString());
        startActivity(loginPage);

    }

    public void turnOffMobileData(){
        /*
        Context context = this;
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, false);

        }
        catch(Exception e){
            System.out.println(e.toString());
        }

        */
    }

    public void monitor(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {
                                Thread.sleep(50);
                            } catch (Exception e) {
                                ;
                            }
                            //System.out.println("Wifi name :"+wifiInfo.getIpAddress());
                            //System.out.println("Wifi ID:"+wifiInfo.getIpAddress()%(Math.pow(256,2)));
                            if(isWifiConnected()) {
                                try {
                                    Thread.sleep(50);
                                } catch (Exception e) {
                                    ;
                                }
                                if (sharedPref.getBoolean("status", false) && isInternetConnected()) {

                                    updateToLogout();
                                } else {
                                    updateToLogin();
                                }
                                deactivateWifiAlert();
                            }
                            else{
                                activateWifiAlert();
                            }
                        }
                    }
                }
        ).start();
    }

    public boolean isWifiConnected() {
        try {
            String command = "ping -c 1 172.18.10.10";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInternetConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void activateWifiAlert(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Connect To VITAP WIFI");
            }
        });
    }

    public void deactivateWifiAlert(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("");
            }
        });
    }

    public void updateToLogin(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                login.setVisibility(View.VISIBLE);
                signout.setVisibility(View.INVISIBLE);
                smartLogout.setVisibility(View.INVISIBLE);
                timerLogout.setVisibility(View.INVISIBLE);
                updater.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void updateToLogout(){
        login.setVisibility(View.INVISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                login.setVisibility(View.INVISIBLE);
                signout.setVisibility(View.VISIBLE);
                smartLogout.setVisibility(View.VISIBLE);
                timerLogout.setVisibility(View.VISIBLE);
                updater.setVisibility(View.VISIBLE);
                if(!sharedPref.getString("regNo","").equals("")) {
                    registration_id.setText(sharedPref.getString("regNo", ""));
                }
            }
        });
    }

    public void timerLogout(View view){
        Intent next = new Intent(this,timerLogout.class);
        next.putExtra("regNo",registration_id.getText().toString());
        startActivity(next);
    }

    public void smartLogout(View view){
        Intent next = new Intent(this,smartLogout.class);
        next.putExtra("regNo",registration_id.getText().toString());
        startActivity(next);
    }

    public void contact(View view){
        Intent next = new Intent(this,developerContact.class);
        startActivity(next);
    }

    public void updateJump(View view){
        startActivity(new Intent(this,updateChecker.class));
    }
}
