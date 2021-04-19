package com.hpx.humanpowerexchange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showProgressDialog("Please wait while fetching user details...");

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        boolean verifiedUser = preferences.getBoolean(HPX_USER_VERIFIED, false);
        String mobile = preferences.getString(HPX_MOBILE_ID, "" );
        String userId = preferences.getString(HPX_USER_ID, "" );

        if (verifiedUser) {
            AppController.getInstance().addToRequestQueue(switchToUserPage(mobile), TAG);
        } else {
            Intent i = new Intent(getApplicationContext(), SendOtpActivity.class);
            startActivity(i);
        }
    }
}