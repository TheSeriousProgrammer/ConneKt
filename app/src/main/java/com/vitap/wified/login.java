package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class login extends AppCompatActivity {
    TextView status ;
    String password ;
    ImageView success ;
    ImageView failed ;
    ProgressBar progressBar ;
    String registration_no;
    Button login ;
    Boolean Autosave;
    Boolean Mode = true ;
    /*
        returns 3 when login succefull
        returns 2 login limit exceeded condition
        returns 1 invalid password
        returns 0 when login time exceeded error
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();

        status  = findViewById(R.id.status);
        success = findViewById(R.id.correct);
        failed = findViewById(R.id.failed);
        progressBar = findViewById(R.id.progressBar);

        registration_no = bundle.getString("regNo");
        password = bundle.getString("pssd");
        Autosave = bundle.getBoolean("state");

        login =  findViewById(R.id.login);
        submitRequest();

    }

    public boolean isInternetConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void submitRequest(){
        //request_thread.start();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                reset();
                requests req = new requests();
                int response = req.loginRequest(registration_no, password);

                if (response == 3) {

                    success();

                } else if (response == 2) {

                    multiLogin();

                } else if (response == 1) {

                    invalidPassword();

                } else if (response == 0) {

                    timeExceeded();

                } else {

                    errorCommunicating();

                }

            }
        };
        new Thread(task).start();
    }


    /*
        UI elements can be updated only in the ui thread , hence the fucntions below make the
        necessary calls in the UI thread, and these functions can be called in the network thread
    */

    public  void reset(){
        Runnable task = new Runnable(){
            public void run() {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                success.setVisibility(View.INVISIBLE);
                failed.setVisibility(View.INVISIBLE);
                status.setText("Waiting for response from server");

            }
        };
        runOnUiThread(task);
    }

    public void success(){

        System.out.println("Waiting for internet connection to be established");
        while(!isInternetConnected());//Waiting Till internet starts to show up
        System.out.println("Internet connection to be established");
        //It takes time for sophos to perfom the operation even after receiving the request, that's why this procedure


        Runnable task = new Runnable() {
            @Override
            public void run() {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setText("Logged in as " + registration_no.trim());
                success.setVisibility(View.VISIBLE);
            }
        };
        runOnUiThread(task);

        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(Autosave) {

            editor.putString("regNo", registration_no);
            editor.putString("pssKey", password);

        }
        editor.putBoolean("status",true);
        editor.putString("lastRegNo",registration_no);
        editor.commit();

    }

    public void multiLogin(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setText("Multiple Devices Login Error");
                failed.setVisibility(View.VISIBLE);
                login.setText("Try Logging out");
                Mode  = false;
                //failed.setVisibility(View.VISIBLE);
            }
        };
        runOnUiThread(task);
    }

    /*
      */
    public void invalidPassword(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setText("Invalid Password");
                failed.setVisibility(View.VISIBLE);

            }
        };
        runOnUiThread(task);
    }

    public void timeExceeded(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setText("Login Time Exceeded");
                failed.setVisibility(View.VISIBLE);

            }
        };
        runOnUiThread(task);
    }

    public void errorCommunicating(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                status.setText("Couldn't Communicate to Portal");
                failed.setVisibility(View.VISIBLE);

                login.setText("retry");
                Mode  = true ;
            }
        };
        runOnUiThread(task);
    }

    public void retry(View view){
        if(Mode){
            submitRequest();
        }
        else{
            Intent next = new Intent(this,logout.class);
            next.putExtra("regNo",registration_no);
            startActivity(next);
        }

    }
}
