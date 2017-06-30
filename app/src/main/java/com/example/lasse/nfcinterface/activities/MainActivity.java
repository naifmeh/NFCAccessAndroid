package com.example.lasse.nfcinterface.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView responseTextView;
    private Button buttonUid,buttonNFC;
    private EditText uidTextField;
    private ImageView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
        responseTextView = (TextView) findViewById(R.id.response_view);
        buttonUid = (Button) findViewById(R.id.sendUid);
        buttonNFC = (Button) findViewById(R.id.buttonNFC);
        uidTextField = (EditText) findViewById(R.id.uidTextField);
        addButton = (ImageView) findViewById(R.id.addBtn);

        //startActivity(new Intent(MainActivity.this,NFCHandleActivity.class));
        URL urlToReq;
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra(NFCHandleActivity.UID_DETECTED_STRING) != null) {
                String uidStr = intent.getStringExtra(NFCHandleActivity.UID_DETECTED_STRING);
                uidStr = uidStr.trim().toLowerCase();

                responseTextView.setText("SYNCING");
                urlToReq = NetworkUtils.getUrl(uidStr);
                getResponseFromHttpUrl(this,urlToReq);
            }

        }
        buttonUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = uidTextField.getText().toString().trim();
                responseTextView.setText("SYNCING");
                URL urlToReq = NetworkUtils.getUrl(uid);

                getResponseFromHttpUrl(MainActivity.super.getApplicationContext(),urlToReq);
            }
        });

        buttonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NFCHandleActivity.class));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        responseTextView.setText("WAITING");



    }

    private void getResponseFromHttpUrl(Context context, URL url) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Log.d(TAG,url.toString());
        StringRequest stringRequest = new StringRequest(url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HTTP RESPONSE--------"+response);

                    if (Integer.parseInt(response.trim()) >= 0) {
                        responseTextView.setText("GRANTED");
                        final int sdk = Build.VERSION.SDK_INT;
                        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                            responseTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_textview));
                        } else {
                            responseTextView.setBackground(getResources().getDrawable(R.drawable.rounded_textview));
                        }
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseTextView.setText("REFUSED");
                final int sdk = Build.VERSION.SDK_INT;
                if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    responseTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_rounded_textview));
                } else {
                    responseTextView.setBackground(getResources().getDrawable(R.drawable.red_rounded_textview));
                }


            }
        }){
            protected Map<String,String> getParams() {
                //data to send
                Map<String,String> data = new HashMap<String,String>();
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }


}
