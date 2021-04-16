package com.hpx.humanpowerexchange;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.CREATE_SERVICE_REQUEST;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_FOR_USER;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_READ_ALL;

public class ServiceRequestActivity extends AppCompatActivity {

    private static final String TAG = ServiceRequestActivity.class.getSimpleName();

    private Button button1;
    private EditText description;
    PopupMenu popup;
    Map<String, Integer> serviceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        String mobile="";
        int requestId = 0;
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
            requestId = extras.getInt("requestId", 0);
        }

        description = findViewById(R.id.editDescription);
        TextView requestIdView = findViewById(R.id.requestIdView);
        requestIdView.setText(requestIdView.getText().toString().replace("@id@", String.valueOf(requestId)));

        button1 = findViewById(R.id.serviceListPickOne);

        serviceMap = new HashMap<>();
        popup = new PopupMenu(ServiceRequestActivity.this, button1);
        popup.getMenuInflater().inflate(R.menu.service_list_pop_up_menu, popup.getMenu());
        AppController.getInstance().addToRequestQueue(fillServiceList());

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        button1.setText(item.getTitle());
                        return true;
                    }
                });
                popup.show();
            }
        });

        Button serviceRequestSave = findViewById(R.id.serviceRequestSave);
        final String finalMobile = mobile;
        serviceRequestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("consumer_mobile", finalMobile);
                    jsonObject.put("service_id", Objects.requireNonNull(serviceMap.get(button1.getText())).intValue());
                    jsonObject.put("description", description.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(createServiceRequest(jsonObject, finalMobile));
            }
        });
    }

    public JsonObjectRequest createServiceRequest(JSONObject jsonObject, final String mobile) {
        Log.d(TAG, jsonObject.toString());
        return new JsonObjectRequest(CREATE_SERVICE_REQUEST, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AppController.getInstance().getApplicationContext(), "Your Request Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        );
    }


    public JsonObjectRequest fillServiceList() {
        return new JsonObjectRequest(SERVICES_READ_ALL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray services = response.optJSONArray("records");
                        if (services != null && services.length() > 0) {

                            for (int i = 0; i < services.length(); i++) {
                                try {
                                    JSONObject obj = services.getJSONObject(i);
                                    serviceMap.put(obj.getString("name"),obj.getInt("id"));
                                    if (i==0) {
                                        popup.getMenu().getItem(0).setTitle(obj.getString("name"));
                                    } else {
                                        popup.getMenu().add(obj.getString("name"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        );

    }
}