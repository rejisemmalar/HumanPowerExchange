package com.hpx.humanpowerexchange.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hpx.humanpowerexchange.AppController;
import com.hpx.humanpowerexchange.R;
import com.hpx.humanpowerexchange.ServiceRequestActivity;
import com.hpx.humanpowerexchange.ServiceRequestEdit;
import com.hpx.humanpowerexchange.restapi.dto.ServiceProviderDto;
import com.hpx.humanpowerexchange.restapi.dto.ServiceRequestDto;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceRequestAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ServiceRequestDto> serviceRequestDtos;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ServiceRequestAdapter(Activity activity, List<ServiceRequestDto> serviceRequestDtos) {
        this.activity = activity;
        this.serviceRequestDtos = serviceRequestDtos;
    }

    @Override
    public int getCount() {
        return serviceRequestDtos.size();
    }

    @Override
    public ServiceRequestDto getItem(int location) {
        return serviceRequestDtos.get(location);
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
            convertView = inflater.inflate(R.layout.service_request_list, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        final TextView serviceRequestId = convertView.findViewById(R.id.serviceRequestId);
        final TextView serviceRequestStatus = convertView.findViewById(R.id.serviceRequestStatus);
        Button serviceDescription = convertView.findViewById(R.id.serviceRequestDetail);

        System.out.println("The serice request size - "+ getCount());
        // getting model data for the row
        ServiceRequestDto serviceRequestDto = serviceRequestDtos.get(position);
        serviceRequestId.setText(String.valueOf(serviceRequestDto.getId()));
        String statusMessage;
        switch (serviceRequestDto.getStatus()) {
            case 2:
                statusMessage = "accepted";
                break;
            case 3:
                statusMessage = "inprogress";
                break;
            case 4:
                statusMessage = "completed";
                break;
            case 5:
                statusMessage = "closed";
                break;
            default:
                statusMessage = "open";
                break;
        }
        serviceRequestStatus.setText(statusMessage);
        serviceDescription.setText(serviceRequestDto.getDescription());
        serviceDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        serviceDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppController.getInstance().getApplicationContext(), ServiceRequestEdit.class);
                intent.putExtra("requestId", serviceRequestId.getText());
                activity.startActivity(intent);
            }
        });

        return convertView;
    }
}
