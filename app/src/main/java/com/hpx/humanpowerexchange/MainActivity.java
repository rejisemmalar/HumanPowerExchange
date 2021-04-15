package com.hpx.humanpowerexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hpx.humanpowerexchange.restapi.dto.UserDto;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONObject;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private int userPageId = 0;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please wait while fetching user details...");
        pDialog.show();

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        boolean verifiedUser = preferences.getBoolean(HPX_USER_VERIFIED, false);
        String mobile = preferences.getString(HPX_MOBILE_ID, "" );

        if (verifiedUser) {
            // go to the activity where user can perform some action
            AppController.getInstance().addToRequestQueue(switchToUserPage(mobile), TAG);
        } else {
            // find the phone number and trigger otp verification process
            Intent i = new Intent(getApplicationContext(), SendOtpActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public JsonObjectRequest switchToUserPage(final String mobile) {
        String url = UrlConstants.READ_USER_BY_MOBILE + "?mobile="+mobile;

        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response), UserDto.class);
                startActivity(getNextActivity(userDto));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public Intent getNextActivity(UserDto userDto) {
        Intent intent;
        switch (userDto.getUser_page()) {
            case USER_DETAILS_PAGE:
                intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                break;
            case SERVICE_PROVIDER_SELECTION_PAGE:
                intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                break;
            case SERVICE_PROVIDER_PAGE:
                intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                break;
            case CONSUMER_PAGE:
                intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                break;
            default:
                intent = new Intent(getApplicationContext(), this.getClass());
        }
        intent.putExtra("mobile", userDto.getMobile());
        return intent;
    }


}