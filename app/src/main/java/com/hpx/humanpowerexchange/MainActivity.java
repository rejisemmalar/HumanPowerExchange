package com.hpx.humanpowerexchange;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void SignUp(View V) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);


    }

    public void Forgotpassword(View V) {
        Intent i = new Intent(this, Forgotpassword.class);
        startActivity(i);
        getActionBar().hide();
    }

}