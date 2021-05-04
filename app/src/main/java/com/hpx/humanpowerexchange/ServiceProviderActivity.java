package com.hpx.humanpowerexchange;

import android.app.ProgressDialog;
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
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.READ_SERVICE_REQUEST_FOR_SERVICE_PROVIDER;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPDATE_USER_PAGE;

public class ServiceProviderActivity extends BaseActivity {

    private static final String TAG = ServiceProviderActivity.class.getSimpleName();

    private List<ServiceRequestDto> serviceRequestList = new ArrayList();
    private ListView listView;
    private ServiceRequestAdapter adapter;
    private String mobile = "";
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

        Button changeToConsumer = findViewById(R.id.changeToConsumer);

        String language = preferences.getString(HPX_USER_LANGUAGE, "en");
        if (language.equalsIgnoreCase("ta")) {
            ((TextView)findViewById(R.id.textView)).setText("சேவை வழங்குநர்");
            ((TextView)findViewById(R.id.existingRequest)).setText("உங்கள் சேவை வழங்கலுக்கான கோரிக்கை பட்டியல்");
            changeToConsumer.setText("நுகர்வோர்");
        }
        showProgressDialog("Loading...");

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        final String finalMobile = mobile;

        AppController.getInstance().addToRequestQueue(fillServiceRequestList(finalMobile));

        changeToConsumer.setOnClickListener(new View.OnClickListener() {
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
    public void onDestroy() {
        super.onDestroy();
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

    public JsonObjectRequest fillServiceRequestList(final String mobile) {

        return new JsonObjectRequest(READ_SERVICE_REQUEST_FOR_SERVICE_PROVIDER, getJsonWithMobile(mobile),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        JSONArray serviceIds = response.optJSONArray("serviceIds");
                        if (serviceIds == null || serviceIds.length() <= 0) {
                            showProgressDialog("No service option given. First select the services you can provide.");
                            Intent intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        } else {
                            JSONArray serviceRequests = response.optJSONArray("service_requests");
                            // Parsing json
                            if (serviceRequests != null && serviceRequests.length() > 0) {
                                hidePDialog();
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


}