package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hpx.humanpowerexchange.adapter.ServiceProviderSelectionAdapter;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;
import com.hpx.humanpowerexchange.restapi.dto.UserDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SAVE_USER_SERVICES;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_FOR_USER;

public class ServiceProviderSelectionActivity extends BaseActivity {

    private static final String TAG = ServiceProviderSelectionActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    private List<ServiceProviderDto> serviceList = new ArrayList();
    private ListView listView;
    private ServiceProviderSelectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_selection);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor =  preferences.edit();
        preferenceEditor.putInt(HPX_USER_PAGE, SERVICE_PROVIDER_SELECTION_PAGE);
        preferenceEditor.commit();

        listView = (ListView) findViewById(R.id.list);
        adapter = new ServiceProviderSelectionAdapter(this, serviceList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        AppController.getInstance().addToRequestQueue(fillServiceList(mobile));

        final String finalMobile = mobile;

        Button saveDetails = findViewById(R.id.save);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mobile",finalMobile);
                    JSONArray jsonArray = new JSONArray();
                    for (ServiceProviderDto serviceProviderDto : serviceList) {
                        if (serviceProviderDto.isSelected()) {
                            jsonArray.put(serviceProviderDto.getId());
                        }
                    }
                    jsonObject.put("serviceIds",jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(saveServiceList(jsonObject));
                Intent intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });

        Button changeToBeConsumer = findViewById(R.id.changeToBeConsumer);
        changeToBeConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getInstance().addToRequestQueue(updateUserPage(finalMobile, CONSUMER_PAGE));
                Intent intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });

    }

    public JsonObjectRequest fillServiceList(final String mobile) {
        String url = SERVICES_FOR_USER + "?mobile="+mobile;
        return new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        JSONArray services = response.optJSONArray("services");
                        // Parsing json
                        if (services != null && services.length() > 0) {
                            for (int i = 0; i < services.length(); i++) {
                                try {
                                    JSONObject obj = services.getJSONObject(i);
                                    ServiceProviderDto serviceProviderDto = new ServiceProviderDto();
                                    serviceProviderDto.setId(obj.getInt("id"));
                                    serviceProviderDto.setName(obj.getString("name"));
                                    serviceProviderDto.setImageUrl(obj.getString("image_url"));
                                    serviceProviderDto.setSelected(obj.getBoolean("selected"));
                                    serviceList.add(serviceProviderDto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // notifying list adapter about data changes so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public JsonObjectRequest saveServiceList(JSONObject jsonObject) {
        return new JsonObjectRequest(SAVE_USER_SERVICES, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
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
    }
}