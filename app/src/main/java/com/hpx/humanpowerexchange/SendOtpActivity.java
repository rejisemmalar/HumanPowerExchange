package com.hpx.humanpowerexchange;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_FOR_USER;

public class SendOtpActivity extends BaseActivity {

    public static final String TAG = SendOtpActivity.class.getSimpleName();

    private int CREDENTIAL_PICKER_REQUEST = 1;
    private TextView txtView;
    private Button button;
    private String language = "en";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        language = preferences.getString(HPX_USER_LANGUAGE, "en");

        txtView = (EditText) findViewById(R.id.editTextPhone);
        button = findViewById(R.id.button2);

        if (language.equalsIgnoreCase("ta")) {
            ((TextView) findViewById(R.id.sendOtpTextView)).setText("தொலைபேசி எண்ணை உள்ளிடவும்/எடுக்கவும்");
            button.setText("otp அனுப்பு");
        }

        phoneSelection();
        enableDisableSendOtpButton();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = txtView.getText().toString();
                AppController.getInstance().addToRequestQueue(sendOtp(mobile), TAG);
            }
        });
    }


    private void phoneSelection() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        CredentialsOptions credentialsOptions = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        CredentialsClient credentialsClient = Credentials.getClient(this.getApplicationContext(), credentialsOptions);
        PendingIntent intent = credentialsClient.getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(
                    intent.getIntentSender(),
                    CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle()
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // Obtain the phone number from the result
    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if (credential != null) {
                String phoneNumber = credential.getId();
                String mobileText = phoneNumber;
                if (phoneNumber.length() == 13 && phoneNumber.substring(0,3).equals("+91")) {
                    mobileText = phoneNumber.substring(3,13);
                } else if (phoneNumber.length() == 11 && phoneNumber.substring(0,1).equals("0")) {
                    mobileText = phoneNumber.substring(1,11);
                }
                txtView.setText(mobileText);
                enableDisableSendOtpButton();
            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show();
            txtView.setHint("Number not found. Type the number");
            if (language.equalsIgnoreCase("ta")) {
                txtView.setHint("எண் கிடைக்கவில்லை. எண்ணைத் தட்டச்சு செய்க");
            }
            button.setEnabled(false);
            enableDisableSendOtpButton();
        }
    }

    public void enableDisableSendOtpButton() {
        txtView.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 10) {
                    button.setEnabled(false);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 10) {
                    button.setEnabled(false);
                }
            }
        });
    }

    public JsonObjectRequest sendOtp(final String mobile) {
        return new JsonObjectRequest(UrlConstants.SEND_OTP_URL, getJsonWithMobile(mobile),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(AppController.getInstance().getApplicationContext(), "Otp send to your mobile number: "+ mobile + " we will read and verify it quickly" , Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), VerifyOtpActivity.class);
                        i.putExtra("mobile", mobile);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AppController.getInstance().getApplicationContext(), "There is some error in sending Otp to your mobile number: "+ mobile , Toast.LENGTH_LONG).show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        );
    }

}