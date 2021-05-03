package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hpx.humanpowerexchange.restapi.dto.UserDto;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.STATUS_ACCEPT;
import static com.hpx.humanpowerexchange.utils.AppConstant.STATUS_CLOSED;
import static com.hpx.humanpowerexchange.utils.AppConstant.STATUS_COMPLETED;
import static com.hpx.humanpowerexchange.utils.AppConstant.STATUS_INPROGRESS;
import static com.hpx.humanpowerexchange.utils.AppConstant.STATUS_OPEN;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;

public class ServiceRequestEdit extends BaseActivity {

    private TextView requestIdView, statusText, consumerNameView, consumerMobileView, providerNameView, providerMobileView, serviceNameView, taskClosedView;
    private EditText editDescription;
    private Button serviceRequestSave, serviceRequestAccept, serviceRequestReject, serviceRequestCompleted;

    int fromUserPage = 2;
    String mobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request_edit);

        requestIdView = findViewById(R.id.requestIdView);
        statusText = findViewById(R.id.statusText);
        consumerNameView = findViewById(R.id.consumerNameView);
        consumerMobileView = findViewById(R.id.consumerMobileView);
        providerNameView = findViewById(R.id.providerNameView);
        providerMobileView = findViewById(R.id.providerMobileView);
        serviceNameView = findViewById(R.id.serviceNameView);
        editDescription = findViewById(R.id.editDescription);

        serviceRequestSave = findViewById(R.id.serviceRequestSave);
        serviceRequestAccept = findViewById(R.id.serviceRequestAccept);
        serviceRequestReject = findViewById(R.id.serviceRequestReject);
        serviceRequestCompleted = findViewById(R.id.serviceRequestCompleted);
        taskClosedView = findViewById(R.id.taskClosedView);

        int requestId = 0;
        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        mobile = preferences.getString(HPX_MOBILE_ID, "" );
        fromUserPage = preferences.getInt(HPX_USER_PAGE, 2);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            requestId = Integer.parseInt(extras.getString("requestId", "0"));
        }

        AppController.getInstance().addToRequestQueue(fillServiceRequestDetails(requestId, fromUserPage));

        final int finalRequestId = requestId;
        serviceRequestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", finalRequestId);
                    jsonObject.put("description", editDescription.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(updateServiceRequest(jsonObject));
            }
        });

        final String finalMobile = mobile;
        final int finalFromUserPage = fromUserPage;
        serviceRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", finalRequestId);
                    if (finalFromUserPage == SERVICE_PROVIDER_PAGE) {
                        jsonObject.put("provider_mobile", finalMobile);
                        jsonObject.put("status", STATUS_ACCEPT);
                    } else if (finalFromUserPage == CONSUMER_PAGE) {
                        jsonObject.put("status", STATUS_INPROGRESS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(updateServiceRequest(jsonObject));
            }
        });

        serviceRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", finalRequestId);
                    jsonObject.put("status", STATUS_OPEN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(updateServiceRequest(jsonObject));
            }
        });

        serviceRequestCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", finalRequestId);
                    jsonObject.put("status", STATUS_COMPLETED);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(updateServiceRequest(jsonObject));
            }
        });

    }

    public JsonObjectRequest fillServiceRequestDetails(final int requestId, final int fromUserPage) {
        return new JsonObjectRequest(UrlConstants.READ_SERVICE_REQUEST_BY_ID + "?id="+Integer.valueOf(requestId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    requestIdView.setText(requestIdView.getText().toString().replace("@id@", String.valueOf(response.getInt("id"))));
                    int status = Integer.parseInt(response.getString("status"));
                    String statusMessage = "Open";
                    if (status == STATUS_ACCEPT) { statusMessage = "Accepted"; }
                    else if (status == STATUS_INPROGRESS) { statusMessage = "Inprogress"; }
                    else if (status == STATUS_COMPLETED) { statusMessage = "Completed"; }
                    else if (status == STATUS_CLOSED) { statusMessage = "Closed"; }
                    statusText.setText(statusMessage);
                    consumerNameView.setText(consumerNameView.getText().toString().replace("@consumerName@", response.getString("consumer_name")));
                    consumerMobileView.setText(consumerMobileView.getText().toString().replace("@consumerMobile@", response.getString("consumer_mobile")));
                    String provider_name = "No Provider Yet";
                    if (!response.getString("provider_name").equals("")) {
                        provider_name = response.getString("provider_name");
                    }
                    String provider_mobile = "No Provider Yet";
                    if (!response.getString("provider_mobile").equals("")) {
                        provider_mobile = response.getString("provider_mobile");
                    }
                    providerNameView.setText(providerNameView.getText().toString().replace("@providerName@", provider_name));
                    providerMobileView.setText(providerMobileView.getText().toString().replace("@providerMobile@", provider_mobile));
                    serviceNameView.setText(serviceNameView.getText().toString().replace("@serviceName@", response.getString("service_name")));
                    editDescription.setText(response.getString("description"));

                    if (fromUserPage == CONSUMER_PAGE) {
                        if (status == STATUS_OPEN) {
                            editDescription.setEnabled(true);
                            serviceRequestSave.setEnabled(true);
                            serviceRequestSave.setVisibility(View.VISIBLE);

                            serviceRequestAccept.setEnabled(false);
                            serviceRequestAccept.setVisibility(View.INVISIBLE);
                            serviceRequestReject.setEnabled(false);
                            serviceRequestReject.setVisibility(View.INVISIBLE);
                            serviceRequestCompleted.setEnabled(false);
                            serviceRequestCompleted.setVisibility(View.INVISIBLE);
                        } else if (status == STATUS_ACCEPT) {
                            editDescription.setEnabled(false);
                            serviceRequestSave.setEnabled(false);
                            serviceRequestSave.setVisibility(View.INVISIBLE);

                            serviceRequestAccept.setEnabled(true);
                            serviceRequestAccept.setVisibility(View.VISIBLE);
                            serviceRequestReject.setEnabled(true);
                            serviceRequestReject.setVisibility(View.VISIBLE);
                            serviceRequestCompleted.setEnabled(false);
                            serviceRequestCompleted.setVisibility(View.INVISIBLE);
                        } else if (status == STATUS_INPROGRESS) {
                            editDescription.setEnabled(false);
                            serviceRequestSave.setEnabled(false);
                            serviceRequestSave.setVisibility(View.INVISIBLE);

                            serviceRequestAccept.setEnabled(false);
                            serviceRequestAccept.setVisibility(View.INVISIBLE);
                            serviceRequestReject.setEnabled(false);
                            serviceRequestReject.setVisibility(View.INVISIBLE);
                            serviceRequestCompleted.setEnabled(true);
                            serviceRequestCompleted.setVisibility(View.VISIBLE);
                        } else if (status == STATUS_COMPLETED || status == STATUS_CLOSED) {
                            editDescription.setEnabled(false);
                            serviceRequestSave.setEnabled(false);
                            serviceRequestSave.setVisibility(View.INVISIBLE);

                            serviceRequestAccept.setEnabled(false);
                            serviceRequestAccept.setVisibility(View.INVISIBLE);
                            serviceRequestReject.setEnabled(false);
                            serviceRequestReject.setVisibility(View.INVISIBLE);
                            serviceRequestCompleted.setEnabled(false);
                            serviceRequestCompleted.setVisibility(View.INVISIBLE);

                            taskClosedView.setVisibility(View.VISIBLE);
                        }

                    } else if (fromUserPage == SERVICE_PROVIDER_PAGE){
                        editDescription.setEnabled(false);
                        serviceRequestSave.setEnabled(false);
                        serviceRequestSave.setVisibility(View.INVISIBLE);

                        serviceRequestReject.setEnabled(false);
                        serviceRequestReject.setVisibility(View.INVISIBLE);

                        serviceRequestCompleted.setEnabled(false);
                        serviceRequestCompleted.setVisibility(View.INVISIBLE);

                        if (status == STATUS_OPEN) {
                            serviceRequestAccept.setEnabled(true);
                            serviceRequestAccept.setVisibility(View.VISIBLE);
                        } else if (status == STATUS_ACCEPT || status == STATUS_INPROGRESS) {
                            serviceRequestAccept.setEnabled(false);
                            serviceRequestAccept.setVisibility(View.INVISIBLE);
                            taskClosedView.setVisibility(View.VISIBLE);
                            taskClosedView.setText("Request already accepted by a provider");
                        }  else if (status == STATUS_COMPLETED || status == STATUS_CLOSED) {
                            taskClosedView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public JsonObjectRequest updateServiceRequest(JSONObject jsonObject) {
        return new JsonObjectRequest(UrlConstants.UPDATE_SERVICE_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(AppController.getInstance().getApplicationContext(), "Request Updated", Toast.LENGTH_SHORT).show();
                startActivity(getNextActivity(mobile, fromUserPage));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}