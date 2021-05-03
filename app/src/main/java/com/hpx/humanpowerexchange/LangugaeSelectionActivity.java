package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;

public class LangugaeSelectionActivity extends AppCompatActivity {

    private Button englishButton, tamilButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_langugae_selection);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        String language = preferences.getString(HPX_USER_LANGUAGE, "en" );

        englishButton = findViewById(R.id.englishButton);
        tamilButton = findViewById(R.id.tamilButton);

        if (language.equalsIgnoreCase("en")) {
            englishButton.setClickable(false);
            tamilButton.setClickable(true);
        } else {
            tamilButton.setClickable(false);
            englishButton.setClickable(true);
        }

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(HPX_USER_LANGUAGE, "en");
                editor.commit();
                LocaleHelper.setLocale(getApplicationContext(), "en");
                finish();
            }
        });

        tamilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(HPX_USER_LANGUAGE, "ta");
                editor.commit();
                LocaleHelper.setLocale(getApplicationContext(), "ta");
                finish();
            }
        });
    }
}