package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CleanerPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_page);
    }
    public void AddServices(View V) {
        Intent i = new Intent(this, ServiceProviders.class);
        startActivity(i);
    }
}