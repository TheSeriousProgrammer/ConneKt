package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class updateChecker extends AppCompatActivity {
    TextView field;
    Button initiateUpdate;
    ProgressBar progressBar ;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_checker);
        field  = findViewById(R.id.textView5);
        initiateUpdate = findViewById(R.id.initiateUpdate);
        progressBar = findViewById(R.id.progressBar2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkForUpdates();
            }
        }).start();
    }

    public void checkForUpdates(){
        requests req = new requests();
        String result = req.justARequest("https://raw.githubusercontent.com/Chidhambararajan/ConneKt/master/releases/latest");
        if (result!=null) {
            String res[] = result.split(" ");
            System.out.println(result);
            if (!res[0].equals("2.1.3")) {
                url = res[1];
                showUpdateMessage();
            }
            else{
                showUptoDateMessage();
            }
        }
    }

    public void showUpdateMessage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                field.setText("Your App version is outdated");
                field.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                initiateUpdate.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showUptoDateMessage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                field.setText("Cheers!! Your App is UptoDate");
                field.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                initiateUpdate.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void startNewApkDownload(View e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }
}
