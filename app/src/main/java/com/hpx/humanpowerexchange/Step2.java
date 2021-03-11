package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Step2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
    }

    public void Consumer(View V) {
        Intent i = new Intent(this,Step3i.class);
        startActivity(i);
    }


    public void ServiceProvider(View V) {
        Intent i =new Intent(this,ServiceProviders.class);
        startActivity(i);
}
}