package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class developerContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_contact);
    }

    public void githubRedirect(View view){

        Intent openURL = new Intent(Intent.ACTION_VIEW);
        openURL.setData(Uri.parse("https://github.com/Chidhambararajan"));
        startActivity(openURL);

    }

    public void instaRedired(View view){

        Intent openURL = new Intent(Intent.ACTION_VIEW);
        openURL.setData(Uri.parse("https://www.instagram.com/code_vectors/"));
        startActivity(openURL);

    }

    public void upiRedirect(View view){
        Intent callUpi = new Intent(Intent.ACTION_VIEW,Uri.parse("upi://pay?pa=7338955561@upi&pn=Chidhambararajan&tn=Contribution+for+ConneKt+App&am=15&INR"));
        startActivity(callUpi);
    }
}
