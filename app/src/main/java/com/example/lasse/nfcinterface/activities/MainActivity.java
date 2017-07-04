package com.example.lasse.nfcinterface.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;
import com.example.lasse.nfcinterface.utils.ParseNFC;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NetworkUtils.ResponseUserExistCallback {

    private static final String TAG = MainActivity.class.getSimpleName();


    private EditText uidFielEditText, lNameFieldEditText;
    private Button buttonConfirm;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private NfcAdapter nfcAdapter;

    private SharedPreferences preferences;

    @Override
    protected void onStart() {
        super.onStart();

        nfcAdapter.enableReaderMode(this, new MyMainNfcListener(), NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_A, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(myToolbar);

        uidFielEditText = (EditText) findViewById(R.id.uidField);
        lNameFieldEditText = (EditText) findViewById(R.id.lastNameField);
        buttonConfirm = (Button) findViewById(R.id.signInButton);
        linearLayout = (LinearLayout) findViewById(R.id.mainContainer);
        progressBar = (ProgressBar) findViewById(R.id.signInProgress);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        preferences = getSharedPreferences(getString(R.string.sharedPrefsKey), Context.MODE_PRIVATE);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                checkUser();
            }
        });


    }

    private void checkUser() {
        URL url = NetworkUtils.getUrl(uidFielEditText.getText().toString().trim().toLowerCase());
        NetworkUtils.getResponseFromHttpUrlForUid(this, url, this);
    }

    @Override
    public void responseUserInDb(JSONObject jobj) {
        if(jobj != null) {
            Log.d(TAG, jobj.toString());
            try {
                String uid = jobj.getString(NetworkUtils.UID_FIELD);
                String name = jobj.getString(NetworkUtils.NAME_FIELD);
                String lastName = jobj.getString(NetworkUtils.LAST_NAME_FIELD);
                int rank = jobj.getInt(NetworkUtils.RANK_FIELD);

                if(lastName.toLowerCase().equals(lNameFieldEditText.getText().toString().trim().toLowerCase())) {

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(NetworkUtils.UID_FIELD, uid);
                    editor.putString(NetworkUtils.NAME_FIELD, name);
                    editor.putString(NetworkUtils.LAST_NAME_FIELD, lastName);
                    editor.putInt(NetworkUtils.RANK_FIELD, rank);
                    editor.commit();
                    Snackbar.make(linearLayout,R.string.welcomeUser,Snackbar.LENGTH_LONG).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            switch(event) {
                                case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                            }
                        }
                    }).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
            Snackbar.make(linearLayout,R.string.userNotFound,Snackbar.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
    }


    private class MyMainNfcListener implements NfcAdapter.ReaderCallback {

        @Override
        public void onTagDiscovered(Tag tag) {
            final Tag finTag = tag;
            Log.d(TAG, "UID DISCOVERED" + (new ParseNFC()).uidHexToStr(tag.getId()));
            cardDiscovered(finTag.getId());


        }

        private void cardDiscovered(byte[] uid) {


            ParseNFC parser = new ParseNFC();
            final String strUid = parser.uidHexToStr(uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uidFielEditText.setText(strUid);
                    uidFielEditText.setFocusable(false);
                    uidFielEditText.setClickable(false);


                }
            });
        }

    }

}