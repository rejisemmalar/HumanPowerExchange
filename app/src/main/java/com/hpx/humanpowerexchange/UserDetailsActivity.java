package com.hpx.humanpowerexchange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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

import static com.hpx.humanpowerexchange.utils.AppConstant.APP_PREFERENCE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_LANGUAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.HPX_USER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;

public class UserDetailsActivity extends BaseActivity {

    public static final String TAG = UserDetailsActivity.class.getSimpleName();

    private EditText nameEdit, emailEdit;
    private EditText addressLine1Edit, addressLine2Edit, cityEdit, stateEdit, pincodeEdit, countryEdit;
    private Button saveDetails;

    private boolean nameFilled, line1Filled, cityFilled, stateFilled, pincodeFilled, countryFilled;
    private TextView headingText, nameText, mobileText, mobiletextHead, emailText, addressText, line1Text, line2Text, cityText, stateText, pincodeText, countryText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor =  preferences.edit();
        preferenceEditor.putInt(HPX_USER_PAGE, USER_DETAILS_PAGE);
        preferenceEditor.commit();
        String language = preferences.getString(HPX_USER_LANGUAGE, "en" );

        if (language.equalsIgnoreCase("ta")) {
            ((TextView)findViewById(R.id.textUDView)).setText("உங்கள் விவரங்களைத் திருத்தவும்");
            ((TextView)findViewById(R.id.usernameText)).setText("*பெயர்:");
            ((TextView)findViewById(R.id.mobile)).setText("கைபேசி எண்:");
            ((TextView)findViewById(R.id.emailText)).setText("மின்னஞ்சல்:");
            ((TextView)findViewById(R.id.addressText)).setText("முகவரி:");
            ((TextView)findViewById(R.id.addressLine1Text)).setText("*வரி1:");
            ((TextView)findViewById(R.id.addressLine2Text)).setText("வரி2:");
            ((TextView)findViewById(R.id.cityText)).setText("*நகரம்:");
            ((TextView)findViewById(R.id.stateText)).setText("*மாநிலம்:");
            ((TextView)findViewById(R.id.pincodeText)).setText("*அஞ்சல் குறியீடு:");
            ((TextView)findViewById(R.id.countryText)).setText("*நாடு:");
        }

        String mobile="";
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
        }
        mobileText = findViewById(R.id.textMobile);
        mobileText.setText(mobile);

        saveDetails = findViewById(R.id.saveUserDetails);
        nameEdit = findViewById(R.id.editName);
        addressLine1Edit = findViewById(R.id.line1edit);
        cityEdit = findViewById(R.id.cityEdit);
        stateEdit = findViewById(R.id.stateEdit);
        pincodeEdit = findViewById(R.id.pincodeEdit);
        countryEdit = findViewById(R.id.countryEdit);
        emailEdit = findViewById(R.id.editEmail);
        addressLine2Edit = findViewById(R.id.line2edit);

        checkRequiredFields();

        JsonObjectRequest jsonObjectRequest = fetchUserDetails(mobile);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);


        nameEdit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameFilled = !(nameEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                nameFilled = !(nameEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                nameFilled = !(nameEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });

        addressLine1Edit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                line1Filled = !(addressLine1Edit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                line1Filled = !(addressLine1Edit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                line1Filled = !(addressLine1Edit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });

        cityEdit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cityFilled = !(cityEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                cityFilled = !(cityEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                cityFilled = !(cityEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });

        stateEdit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stateFilled = !(stateEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                stateFilled = !(stateEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                stateFilled = !(stateEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });

        pincodeEdit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pincodeFilled = !(pincodeEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                pincodeFilled = !(pincodeEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                pincodeFilled = !(pincodeEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });

        countryEdit.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countryFilled = !(countryEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                countryFilled = !(countryEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                countryFilled = !(countryEdit.toString().trim().isEmpty());
                checkRequiredFields();
            }
        });


        final String finalMobile = mobile;
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("mobile", finalMobile);
                    jsonBody.put("name", nameEdit.getText());
                    jsonBody.put("email", emailEdit.getText());
                    jsonBody.put("address_line_1", addressLine1Edit.getText());
                    jsonBody.put("address_line_2", addressLine2Edit.getText());
                    jsonBody.put("city", cityEdit.getText());
                    jsonBody.put("state", stateEdit.getText());
                    jsonBody.put("pincode", pincodeEdit.getText());
                    jsonBody.put("country", countryEdit.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = saveUserDetails(jsonBody);
                AppController.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu1).setVisible(false);
        menu.findItem(R.id.menu2).setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    private void checkRequiredFields() {
        if (nameFilled && line1Filled && cityFilled && stateFilled && pincodeFilled && countryFilled) {
            saveDetails.setEnabled(true);
            saveDetails.setClickable(true);
        } else {
            saveDetails.setEnabled(false);
            saveDetails.setClickable(false);
        }
    }

    private JsonObjectRequest saveUserDetails(JSONObject jsonBody) {
        return new JsonObjectRequest(UrlConstants.EDIT_USER, jsonBody, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response),UserDto.class);
                if (userDto!= null && userDto.getId() > 0) {
                    Intent intent = getNextActivity(userDto.getMobile(), userDto.getUser_page());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "There is some error in saving user details" , Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is some error in saving user details" , Toast.LENGTH_LONG).show();

            }
        });
    }

    public JsonObjectRequest fetchUserDetails(final String mobile) {
        String url = UrlConstants.READ_USER_BY_MOBILE + "?mobile="+mobile;

        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response), UserDto.class);
                nameEdit.setText(userDto.getName());
                mobileText.setText(mobile);
                emailEdit.setText(userDto.getEmail());
                addressLine1Edit.setText(userDto.getAddress_line_1());
                addressLine2Edit.setText(userDto.getAddress_line_2());
                cityEdit.setText(userDto.getCity());
                stateEdit.setText(userDto.getState());
                pincodeEdit.setText(userDto.getPincode());
                countryEdit.setText(userDto.getCountry());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}