package com.hpx.humanpowerexchange.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hpx.humanpowerexchange.AppController;
import com.hpx.humanpowerexchange.R;
import com.hpx.humanpowerexchange.restapi.dto.VerifiedOtpDto;

public class VerifyOtpAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private VerifiedOtpDto verifiedOtpDto;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public VerifyOtpAdapter(Activity activity, VerifiedOtpDto verifiedOtpDto) {
        this.activity = activity;
        this.verifiedOtpDto = verifiedOtpDto;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return verifiedOtpDto;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_verify_otp, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView otpMessage = convertView.findViewById(R.id.verifiedTextView);

        // getting model data for the row
        VerifiedOtpDto verifiedOtpDto = (VerifiedOtpDto) getItem(position);

        // title
        otpMessage.setText(verifiedOtpDto.isOtpVerified() ? "Otp verified Successfully" : "There is some error in verifying otp");

        if (verifiedOtpDto.isOtpVerified()) {

        }

        return convertView;
    }
}
