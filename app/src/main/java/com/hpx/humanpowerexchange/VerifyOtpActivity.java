package com.hpx.humanpowerexchange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_MOBILE_ID;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_VERIFIED;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;

public class VerifyOtpActivity extends AppCompatActivity {

    public static final String TAG = VerifyOtpActivity.class.getSimpleName();

    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six;
    Button verify_otp;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        textView = findViewById(R.id.verifiedTextView);

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);
        verify_otp = findViewById(R.id.verify_otp_btn);

        startSMSListener();

        final EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));


        final String finalMobile = mobile;
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder otp = new StringBuilder();
                for (EditText editText : edit) {
                    otp.append(editText.getText());
                }
                JsonObjectRequest jsonObjectRequest = verifyOtp(finalMobile, otp.toString());
                AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
            }
        });

        final Button resendButton = findViewById(R.id.resend_otp_btn);
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resendButton.setEnabled(true); // The button is enabled by the timer after 2 mins //
                    }
                });
            }
        }, 30*1000);

    }

    private void startSMSListener() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                Toast.makeText(getApplicationContext(), "Retrived the otp", Toast.LENGTH_SHORT).show();

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Toast.makeText(getApplicationContext(), "Not able to retrive the otp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public JsonObjectRequest verifyOtp(final String mobile, String otp) {
        JsonObjectRequest jsonObjectRequest = null;
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("mobile", mobile);
            jsonBody.put("code", otp);

            jsonObjectRequest = new JsonObjectRequest(UrlConstants.VERIFY_OTP_URL, jsonBody, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean otpVerified = response.getBoolean("otpVerified");
                        int userPageId = response.getInt("userPage");
                        if (otpVerified) {
                            textView.setText("Otp verified successfully");
                            textView.setVisibility(View.VISIBLE);

                            SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor preferenceEditor =  preferences.edit();
                            preferenceEditor.putBoolean(HPX_USER_VERIFIED, true);
                            preferenceEditor.putString(HPX_MOBILE_ID, mobile);
                            preferenceEditor.commit();
                            Intent intent;
                            switch (userPageId) {
                                case USER_DETAILS_PAGE:
                                    intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                                    startActivity(intent);
                                    break;
                                case SERVICE_PROVIDER_SELECTION_PAGE:
                                    intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                                    startActivity(intent);
                                    break;
                                case SERVICE_PROVIDER_PAGE:
                                    intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                                    startActivity(intent);
                                    break;
                                case CONSUMER_PAGE:
                                    intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    intent = new Intent(getApplicationContext(), this.getClass());
                            }
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                        } else {
                            textView.setText("Otp verified UnSuccessful");
                            textView.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "There is some error in verifying Otp" , Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText("There is an error in verifying otp. please click resend otp method and verify it");
                    textView.setVisibility(View.VISIBLE);

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectRequest;
    }
}