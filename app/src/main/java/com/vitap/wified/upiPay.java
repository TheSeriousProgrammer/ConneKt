package com.vitap.wified;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class upiPay extends AppCompatActivity {

    EditText amount ;
    TextView status ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_pay);
        amount = findViewById(R.id.editText);
        status = findViewById(R.id.status);
        status.setText(" ");
    }

    public void pay(View view){
        int num = Integer.parseInt(amount.getText().toString());
        if (num<=1) {
            status.setText("Enter an amount above 1 Rs");
        }
        else{
            Intent callUpi = new Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay?pa=7338955561@upi&pn=Chidhambararajan NRM&tn=Contribution+for+ConneKt+App&am=15&INR"));
            startActivity(callUpi);
        }
    }
}
