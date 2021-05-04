package com.hpx.humanpowerexchange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hpx.humanpowerexchange.adapter.ServiceProviderSelectionAdapter;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SAVE_USER_SERVICES;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_FOR_USER;

public class ServiceProviderSelectionActivity extends BaseActivity {

    private static final String TAG = ServiceProviderSelectionActivity.class.getSimpleName();


    private List<ServiceProviderDto> serviceList = new ArrayList();
    private ListView listView;
    private ServiceProviderSelectionAdapter adapter;

    private String mobile="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_selection);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor =  preferences.edit();
        preferenceEditor.putInt(HPX_USER_PAGE, SERVICE_PROVIDER_SELECTION_PAGE);
        preferenceEditor.commit();

        String language = preferences.getString(HPX_USER_LANGUAGE, "en");
        Button saveDetails = findViewById(R.id.save);
        Button changeToBeConsumer = findViewById(R.id.changeToBeConsumer);

        if (language.equalsIgnoreCase("ta")) {
            ((TextView)findViewById(R.id.textView)).setText("நீங்கள் வழங்கக்கூடிய சேவைகளைத் தேர்ந்தெடுக்கவும்");
            saveDetails.setText("சேமி");
            changeToBeConsumer.setText("நுகர்வோர்");
        }

        listView = (ListView) findViewById(R.id.list);
        adapter = new ServiceProviderSelectionAdapter(this, serviceList);
        listView.setAdapter(adapter);

       showProgressDialog("Loading...");

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        AppController.getInstance().addToRequestQueue(fillServiceList(mobile));

        final String finalMobile = mobile;

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu1).setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        String title = String.valueOf(item.getTitle());
        if ("Edit User Details".equalsIgnoreCase(title)) {
            Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
            intent.putExtra("mobile", mobile);
            startActivity(intent);
        }
        return true;
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