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
        openURL.setData(Uri.parse("https://instagram.com/chidhambararajan_code_machine?igshid=2rgbxa0etjww"));
        startActivity(openURL);

    }
}
