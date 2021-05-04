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
import com.hpx.humanpowerexchange.adapter.ServiceRequestAdapter;
import com.hpx.humanpowerexchange.restapi.dto.ServiceRequestDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.READ_SERVICE_REQUEST;

public class ConsumerActivity extends BaseActivity {

    private static final String TAG = ConsumerActivity.class.getSimpleName();

    private List<ServiceRequestDto> serviceRequestList = new ArrayList();
    private ListView listView;
    private ServiceRequestAdapter adapter;
    private String mobile = "";

    private TextView emptyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor =  preferences.edit();
        preferenceEditor.putInt(HPX_USER_PAGE, CONSUMER_PAGE);
        preferenceEditor.commit();

        listView = (ListView) findViewById(R.id.listServiceRequest);
        adapter = new ServiceRequestAdapter(this, serviceRequestList);
        listView.setAdapter(adapter);
        emptyRequest = findViewById(R.id.emptyRequest);

        Button createNewServiceRequest = findViewById(R.id.createServiceRequest);
        Button changeToBeSP = findViewById(R.id.changeToBeSP);


        String language = preferences.getString(HPX_USER_LANGUAGE, "en");
        if (language.equalsIgnoreCase("ta")) {
            ((TextView)findViewById(R.id.textView)).setText("நுகர்வோர்");
            ((TextView)findViewById(R.id.createServiceRequest)).setText("புதிய சேவை கோரிக்கை உருவாக்க");
            changeToBeSP.setText("சேவை வழங்குநர்");
        }

        showProgressDialog("Loading...");

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        AppController.getInstance().addToRequestQueue(fillServiceRequestList(mobile));

        final String finalMobile = mobile;
        changeToBeSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getInstance().addToRequestQueue(updateUserPage(finalMobile, SERVICE_PROVIDER_PAGE));
                Intent intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });

        createNewServiceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ServiceRequestActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu1).setVisible(true);
        menu.findItem(R.id.menu2).setVisible(true);
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
        } else if ("Service Provision Select".equalsIgnoreCase(title)) {
            Intent intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
            intent.putExtra("mobile", mobile);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public JsonObjectRequest fillServiceRequestList(final String mobile) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("consumer_mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(READ_SERVICE_REQUEST, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
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

}