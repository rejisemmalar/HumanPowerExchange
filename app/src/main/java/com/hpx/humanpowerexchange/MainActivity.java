package com.hpx.humanpowerexchange;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public JsonObjectRequest switchToUserPage(final String mobile) {
        String url = UrlConstants.READ_USER_BY_MOBILE + "?mobile="+mobile;

        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response), UserDto.class);
                userPageId = userDto.getUser_page();
                Intent intent;
                switch (userPageId) {
                    case USER_DETAILS_PAGE:
                        intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                        startActivity(intent);
                        break;
                    case SERVICE_PROVIDER_SELECTION_PAGE:
                        intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                        startActivity(intent);
                        break;
                    case SERVICE_PROVIDER_PAGE:
                        intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                        startActivity(intent);
                        break;
                    case CONSUMER_PAGE:
                        intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        intent = new Intent(getApplicationContext(), this.getClass());
                }
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }



}