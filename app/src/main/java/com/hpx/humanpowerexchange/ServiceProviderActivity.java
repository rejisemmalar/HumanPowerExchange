package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPDATE_USER_PAGE;

public class ServiceProviderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);

        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        // AppController.getInstance().addToRequestQueue(fillServiceRequestList(mobile));

        final String finalMobile = mobile;
        Button changeToConsumer = findViewById(R.id.changeToConsumer);
        changeToConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mobile",finalMobile);
                    jsonObject.put("user_page", CONSUMER_PAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(updateUserPage(jsonObject));
                Intent intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });
    }


    public JsonObjectRequest updateUserPage(JSONObject jsonObject) {
        return new JsonObjectRequest(UPDATE_USER_PAGE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
    }
}