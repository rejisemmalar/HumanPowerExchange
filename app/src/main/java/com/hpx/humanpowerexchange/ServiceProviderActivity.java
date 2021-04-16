package com.hpx.humanpowerexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hpx.humanpowerexchange.adapter.ServiceRequestAdapter;
import com.hpx.humanpowerexchange.restapi.dto.ServiceRequestDto;
import com.hpx.humanpowerexchange.restapi.dto.UserDto;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.READ_SERVICE_REQUEST_FOR_SERVICE_PROVIDER;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPDATE_USER_PAGE;

public class ServiceProviderActivity extends AppCompatActivity {

    private static final String TAG = ServiceProviderActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    private List<ServiceRequestDto> serviceRequestList = new ArrayList();
    private ListView listView;
    private ServiceRequestAdapter adapter;

    private TextView emptyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor =  preferences.edit();
        preferenceEditor.putInt(HPX_USER_PAGE, SERVICE_PROVIDER_PAGE);
        preferenceEditor.commit();

        listView = findViewById(R.id.listServiceRequest);
        adapter = new ServiceRequestAdapter(this, serviceRequestList);
        listView.setAdapter(adapter);
        emptyRequest = findViewById(R.id.emptyRequest);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        final String finalMobile = mobile;

        AppController.getInstance().addToRequestQueue(fillServiceRequestList(finalMobile));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*public JsonObjectRequest getServiceList(final String mobile) {
        String url = SERVICES_FOR_USER + "?mobile="+mobile;
        return new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        JSONArray services = response.optJSONArray("services");
                        // Parsing json
                        if (services != null && services.length() > 0) {
                            for (int i = 0; i < services.length(); i++) {
                                try {
                                    JSONObject obj = services.getJSONObject(i);
                                    if (obj.getBoolean("selected")) {
                                        serviceList.add(obj.getInt("id"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (serviceList.isEmpty()) {
                            pDialog.setMessage("No service option given. First select the services you can provide.");
                            pDialog.show();
                            Intent intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        }
                        hidePDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        }
        );
    }*/

    public JsonObjectRequest fillServiceRequestList(final String mobile) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(READ_SERVICE_REQUEST_FOR_SERVICE_PROVIDER, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        JSONArray serviceIds = response.optJSONArray("serviceIds");
                        if (serviceIds == null || serviceIds.length() <= 0) {
                            pDialog = new ProgressDialog(getApplicationContext());
                            pDialog.setMessage("No service option given. First select the services you can provide.");
                            pDialog.show();
                            Intent intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        } else {
                            JSONArray serviceRequests = response.optJSONArray("service_requests");
                            // Parsing json
                            if (serviceRequests != null && serviceRequests.length() > 0) {
                                for (int i = 0; i < serviceRequests.length(); i++) {
                                    emptyRequest.setVisibility(View.INVISIBLE);
                                    try {
                                        JSONObject obj = serviceRequests.getJSONObject(i);
                                        ServiceRequestDto serviceRequestDto = new ServiceRequestDto();
                                        serviceRequestDto.setId(obj.getInt("id"));
                                        serviceRequestDto.setStatus(obj.getInt("status"));
                                        serviceRequestDto.setDescription(obj.getString("description"));
                                        serviceRequestList.add(serviceRequestDto);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                emptyRequest.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        }
        );
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

    /*public JsonObjectRequest fetchUserDetails(final String mobile) {
        String url = UrlConstants.READ_USER_BY_MOBILE + "?mobile="+mobile;

        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response), UserDto.class);
                userCity = userDto.getCity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }*/
}