package com.hpx.humanpowerexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hpx.humanpowerexchange.utils.UrlConstants.CREATE_SERVICE_REQUEST;
import static com.hpx.humanpowerexchange.utils.UrlConstants.SERVICES_READ_ALL;
import static com.hpx.humanpowerexchange.utils.UrlConstants.UPLOAD_FILES_SERVICE_REQUEST;

public class ServiceRequestActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ServiceRequestActivity.class.getSimpleName();
    private static final int IMAGE1_REQUEST_CODE = 111;
    private static final int IMAGE2_REQUEST_CODE = 112;
    private static final int IMAGE3_REQUEST_CODE = 113;

    private Button button1;
    private EditText description;
    PopupMenu popup;
    Map<String, Integer> serviceMap;


    private Chronometer chronometer;
    private ImageView imageViewRecord, imageViewPlay;
    private SeekBar seekBar;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String audioFileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private boolean isPlaying = false;
    private boolean isRecording = false;

    private ImageView requestImage1, requestImage2, requestImage3;
    private Bitmap image1, image2, image3;
    private RelativeLayout relativeLayoutRequestImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        String mobile="";
        int requestId = 0;
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile", "");
            requestId = extras.getInt("requestId", 0);
        }

        description = findViewById(R.id.editDescription);
        TextView requestIdView = findViewById(R.id.requestIdView);
        requestIdView.setText(requestIdView.getText().toString().replace("@id@", String.valueOf(requestId)));

        button1 = findViewById(R.id.serviceListPickOne);

        serviceMap = new HashMap<>();
        popup = new PopupMenu(ServiceRequestActivity.this, button1);
        popup.getMenuInflater().inflate(R.menu.service_list_pop_up_menu, popup.getMenu());
        AppController.getInstance().addToRequestQueue(fillServiceList());

        initViews();

        relativeLayoutRequestImage = findViewById(R.id.images);
        int eachImageWidth = (relativeLayoutRequestImage.getWidth() / 3) - 30;
        requestImage1 = findViewById(R.id.requestImage1);
        requestImage2 = findViewById(R.id.requestImage2);
        requestImage3 = findViewById(R.id.requestImage3);
        requestImage1.getLayoutParams().width = eachImageWidth;
        requestImage2.getLayoutParams().width = eachImageWidth;
        requestImage3.getLayoutParams().width = eachImageWidth;
        requestImage1.setLayoutParams(requestImage1.getLayoutParams());
        requestImage2.setLayoutParams(requestImage2.getLayoutParams());
        requestImage3.setLayoutParams(requestImage3.getLayoutParams());

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        button1.setText(item.getTitle());
                        return true;
                    }
                });
                popup.show();
            }
        });

        requestImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMAGE1_REQUEST_CODE);
            }
        });

        requestImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMAGE2_REQUEST_CODE);
            }
        });

        requestImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMAGE3_REQUEST_CODE);
            }
        });

        Button serviceRequestSave = findViewById(R.id.serviceRequestSave);
        final String finalMobile = mobile;
        serviceRequestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("consumer_mobile", finalMobile);
                    jsonObject.put("service_id", Objects.requireNonNull(serviceMap.get(button1.getText())).intValue());
                    if (description.getText().toString().isEmpty() && audioFileName != null) {
                        description.setText("Audio Description");
                    }
                    jsonObject.put("description", description.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppController.getInstance().addToRequestQueue(createServiceRequest(jsonObject, finalMobile));
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (data.getExtras() != null && data.getExtras().get("data") != null) {
            Bitmap bitmap=(Bitmap) data.getExtras().get("data");
            if (requestCode == IMAGE1_REQUEST_CODE) {
                image1 = bitmap;
                requestImage1.setImageBitmap(bitmap);
            } else if (requestCode == IMAGE2_REQUEST_CODE) {
                image2 = bitmap;
                requestImage2.setImageBitmap(bitmap);
            } else if (requestCode == IMAGE3_REQUEST_CODE) {
                image3 = bitmap;
                requestImage3.setImageBitmap(bitmap);
            }
        }
    }

    public JsonObjectRequest createServiceRequest(JSONObject jsonObject, final String mobile) {
        Log.d(TAG, jsonObject.toString());
        return new JsonObjectRequest(CREATE_SERVICE_REQUEST, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int requestId = 0;
                        try {
                            requestId = response.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (requestId > 0 && (audioFileName != null || image1 !=null || image2 !=null || image3 != null)) {
                            AppController.getInstance().addToRequestQueue(uploadTheFiles(mobile, requestId));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        );
    }

    public VolleyMultipartRequest uploadTheFiles(final String mobile, final int requestId) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_FILES_SERVICE_REQUEST,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(AppController.getInstance().getApplicationContext(), "Your Request Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                        intent.putExtra("mobile", mobile);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (image1 != null) {
                    params.put("image[0]", new DataPart(imagename + "image1.png", getFileDataFromDrawable(image1)));
                    if (image2 !=null) {
                        params.put("image[1]", new DataPart(imagename + "image2.png", getFileDataFromDrawable(image2)));
                        if (image3 !=null) {
                            params.put("image[2]", new DataPart(imagename + "image3.png", getFileDataFromDrawable(image2)));
                        }
                    }
                }
                if (audioFileName != null) {
                    params.put("audio", new DataPart(imagename + "audio.mp3",getAudioFileByteArray(audioFileName)));
                }
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("requestId", String.valueOf(requestId));
                return params;
            }
        };
        return volleyMultipartRequest;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getAudioFileByteArray(String fileName) {
        int bytesRead, bytesAvailable, bufferSize;
        int maxBufferSize = 1024 * 1024;
        Log.d(TAG, "getAudioFileByteArray: "+fileName);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                bos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            Log.d(TAG, "getAudioFileByteArray: byte array size - "+ bos.size());
            return bos.toByteArray();
        } catch (Exception e) {
            Log.d("mylog", e.toString());
        }
        return null;
    }

    public JsonObjectRequest fillServiceList() {
        return new JsonObjectRequest(SERVICES_READ_ALL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray services = response.optJSONArray("records");
                        if (services != null && services.length() > 0) {

                            for (int i = 0; i < services.length(); i++) {
                                try {
                                    JSONObject obj = services.getJSONObject(i);
                                    serviceMap.put(obj.getString("name"),obj.getInt("id"));
                                    if (i==0) {
                                        popup.getMenu().getItem(0).setTitle(obj.getString("name"));
                                    } else {
                                        popup.getMenu().add(obj.getString("name"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        );

    }

    private void initViews() {

        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        imageViewRecord = (ImageView) findViewById(R.id.imageViewRecord);
        imageViewPlay = (ImageView) findViewById(R.id.imageViewPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        imageViewRecord.setOnClickListener(this);
        imageViewPlay.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if( view == imageViewRecord ){
            if (!isRecording) {
                isRecording = true;
                imageViewRecord.setImageResource(R.drawable.ic_stop);
                startRecording();
            } else {
                isRecording = false;
                imageViewRecord.setImageResource(R.drawable.ic_microphone);
                stopRecording();
            }
        } else if( view == imageViewPlay ){
            if( !isPlaying && audioFileName != null ){
                isPlaying = true;
                startPlaying();
            }else{
                isPlaying = false;
                stopPlaying();
            }
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/hpx/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }

        audioFileName =  root.getAbsolutePath() + "/hpx/Audios/" + System.currentTimeMillis() + ".mp3";
        Log.d("filename", audioFileName);
        mRecorder.setOutputFile(audioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        seekBar.setProgress(0);
        stopPlaying();
        //starting the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopRecording() {
        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show();
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        imageViewPlay.setImageResource(R.drawable.ic_pause_audio);
        seekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        seekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        chronometer.start();

        /** once the audio is complete, timer is stopped here**/
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imageViewPlay.setImageResource(R.drawable.ic_play);
                isPlaying = false;
                seekBar.setProgress(0);
                lastProgress=0;
                mp.seekTo(lastProgress);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
            }
        });

        /** moving the track as per the seekBar's position**/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( mPlayer!=null && fromUser ){
                    //here the track's progress is being changed as per the progress bar
                    mPlayer.seekTo(progress);
                    //timer is being updated as per the progress of the seekbar
                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void stopPlaying() {
        try{
            mPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        imageViewPlay.setImageResource(R.drawable.ic_play);
        chronometer.stop();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if(mPlayer != null){
            int mCurrentPosition = mPlayer.getCurrentPosition();
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }
}