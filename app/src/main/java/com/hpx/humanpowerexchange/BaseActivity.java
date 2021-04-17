package com.hpx.humanpowerexchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hpx.humanpowerexchange.restapi.dto.UserDto;
import com.hpx.humanpowerexchange.utils.UrlConstants;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hpx.humanpowerexchange.utils.AppConstant.CONSUMER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.SERVICE_PROVIDER_SELECTION_PAGE;
import static com.hpx.humanpowerexchange.utils.AppConstant.USER_DETAILS_PAGE;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPDATE_USER_PAGE;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog pDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    public void showProgressDialog(String message) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(message);
        pDialog.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Toast.makeText(this, "Clicked: Menu No. 1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu2:
                Toast.makeText(this, "Clicked: Menu No. 2", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu3:
                Toast.makeText(this, "Clicked: Menu No. 2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public JsonObjectRequest updateUserPage(String finalMobile, int userPageId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile",finalMobile);
            jsonObject.put("user_page", userPageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JsonObjectRequest(UPDATE_USER_PAGE, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    public JsonObjectRequest switchToUserPage(final String mobile) {
        String url = UrlConstants.READ_USER_BY_MOBILE + "?mobile="+mobile;

        return new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserDto userDto = new Gson().fromJson(String.valueOf(response), UserDto.class);
                startActivity(getNextActivity(userDto.getMobile(), userDto.getUser_page()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public Intent getNextActivity(String mobile, int userPageId) {
        Intent intent;
        switch (userPageId) {
            case USER_DETAILS_PAGE:
                intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                break;
            case SERVICE_PROVIDER_SELECTION_PAGE:
                intent = new Intent(getApplicationContext(), ServiceProviderSelectionActivity.class);
                break;
            case SERVICE_PROVIDER_PAGE:
                intent = new Intent(getApplicationContext(), ServiceProviderActivity.class);
                break;
            case CONSUMER_PAGE:
                intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                break;
            default:
                intent = new Intent(getApplicationContext(), this.getClass());
        }
        intent.putExtra("mobile", mobile);
        return intent;
    }

}