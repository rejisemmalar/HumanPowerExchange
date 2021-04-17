package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hpx.humanpowerexchange.adapter.ServiceProviderSelectionAdapter;
import com.hpx.humanpowerexchange.adapter.ServiceRequestAdapter;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;
import com.hpx.humanpowerexchange.restapi.dto.ServiceRequestDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.READ_SERVICE_REQUEST;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_FOR_USER;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPDATE_USER_PAGE;

public class ConsumerActivity extends BaseActivity {

    private static final int MENU3 = 1;

    private static final String TAG = ConsumerActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    private List<ServiceRequestDto> serviceRequestList = new ArrayList();
    private ListView listView;
    private ServiceRequestAdapter adapter;

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

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        AppController.getInstance().addToRequestQueue(fillServiceRequestList(mobile));

        final String finalMobile = mobile;
        Button changeToBeSP = findViewById(R.id.changeToBeSP);
        changeToBeSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getInstance().addToRequestQueue(updateUserPage(finalMobile, SERVICE_PROVIDER_PAGE));
                Intent intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });

        Button createNewServiceRequest = findViewById(R.id.createServiceRequest);
        createNewServiceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ServiceRequestActivity.class);
                intent.putExtra("mobile", finalMobile);
                startActivity(intent);
            }
        });
    }

    public JsonObjectRequest fillServiceRequestList(final String mobile) {
        JSONObject jsonObject = new JSONObject();
        try {
            //jsonObject.put("consumer_id", 2); // need to remove this line after testing
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



}