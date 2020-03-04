package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class logout extends AppCompatActivity {
    TextView status ;
    ProgressBar progressBar ;
    ImageView success ;
    ImageView failed;
    int VISIBLE ;
    int INVISIBLE ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        status = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar);
        success = findViewById(R.id.correct);
        failed = findViewById(R.id.failed);
        Bundle bundle = getIntent().getExtras();

        VISIBLE = View.VISIBLE ;
        INVISIBLE = View.INVISIBLE ;

        final String registration_no = bundle.getString("regNo");

        Runnable netWorkTask = new Runnable() {
            @Override
            public void run() {
                requests req = new requests();
                if(req.logoutRequest(registration_no)) {
                    success();
                }
                else {
                    failed();
                }
            }
        };
        new Thread(netWorkTask).start();

    }

    public boolean isInternetConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void success(){

        System.out.println("Waiting till internet connection is terminated");
        while(isInternetConnected());//Waiting till internet connection is stopped
        //It takes time for sophos to perfom the operation even after receiving the request, that's why this procedure
        System.out.println("Internet connection succefully terminated");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Logout Request Sent");
                success.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
                SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("status",false);
                editor.commit();

            }
        });
    }

    public void failed(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Unable to Communicate to Server");
                failed.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }
        });
    }
}
