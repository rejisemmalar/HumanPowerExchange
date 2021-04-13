package com.hpx.humanpowerexchange.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.DrawableUtils;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hpx.humanpowerexchange.AppController;
import com.hpx.humanpowerexchange.R;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderSelectionAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ServiceProviderDto> serviceProviderDtos;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ServiceProviderSelectionAdapter(Activity activity, List<ServiceProviderDto> serviceProviderDtos) {
        this.activity = activity;
        this.serviceProviderDtos = serviceProviderDtos;
    }

    @Override
    public int getCount() {
        return serviceProviderDtos.size();
    }

    @Override
    public ServiceProviderDto getItem(int location) {
        return serviceProviderDtos.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.service_provider_list, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        final CheckBox serviceName = convertView.findViewById(R.id.serviceName);
        CircleImageView serviceImage = convertView.findViewById(R.id.serviceImage);

        // getting model data for the row
        ServiceProviderDto serviceProviderDto = serviceProviderDtos.get(position);
        serviceName.setText(serviceProviderDto.getName());
        serviceName.setChecked(serviceProviderDto.isSelected());
        Context cx = AppController.getInstance().getApplicationContext();
        int id = cx.getResources().getIdentifier(serviceProviderDto.getImageUrl(), "drawable", cx.getPackageName());
        serviceImage.setImageResource(id);

        serviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(position).setSelected(((CheckBox)v).isChecked());
            }
        });

        return convertView;
    }
}
