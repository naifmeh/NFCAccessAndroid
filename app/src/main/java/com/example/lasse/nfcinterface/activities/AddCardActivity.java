package com.example.lasse.nfcinterface.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.network.NetworkUtils;
import com.example.lasse.nfcinterface.utils.ParseNFC;
import com.example.lasse.nfcinterface.views.PulsatingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AddCardActivity extends AppCompatActivity implements NetworkUtils.ResponseHttpCallback{

    private static final String TAG = AddCardActivity.class.getSimpleName();



    private PulsatingView pulsatingView;
    private TextView scanningText;
    private EditText inputUID,inputName,inputLName;

    private Spinner rankChooserSpinner;
    private Button btnAddUsr;

    private CoordinatorLayout coordinatorLayout;

    private LinearLayoutCompat linearLayoutFormContainer;

    private NfcAdapter nfcAdapter;

    private SharedPreferences sharedPreferences;

    private int rankUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarAddingCard);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefsKey), Context.MODE_PRIVATE);
        if(sharedPreferences == null && !sharedPreferences.contains(NetworkUtils.UID_FIELD))
            startActivity(new Intent(AddCardActivity.this,MainActivity.class));

        this.rankUser = sharedPreferences.getInt(NetworkUtils.RANK_FIELD,-1);
        Log.d(TAG,"USER RANK : "+String.valueOf(rankUser));
        if(this.rankUser<2)  {
            Log.d(TAG,"ERROR USER NOT PRIVLIGED");
            Toast.makeText(this,getString(R.string.errorPrivilege),Toast.LENGTH_LONG).show();
            startActivity(new Intent(AddCardActivity.this,ProfileActivity.class));

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.container_add_usr);
        linearLayoutFormContainer = (LinearLayoutCompat) findViewById(R.id.container_form_addUsr);

        inputUID =(EditText) findViewById(R.id.input_uid);
        inputName = (EditText) findViewById(R.id.input_name);
        inputLName = (EditText) findViewById(R.id.input_lname);
        rankChooserSpinner = (Spinner) findViewById(R.id.rankSpinnerUsr);
        btnAddUsr = (Button) findViewById(R.id.btnAddUsr);


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.addUsrRanklvl0,android.R.layout.simple_spinner_item);;
        switch (this.rankUser) {
            case 2:
                spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.addUsrRanklvl2,android.R.layout.simple_spinner_item);
                break;
            case 3:
                spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.addUsrRanklvl3,android.R.layout.simple_spinner_item);
                break;
        }


        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rankChooserSpinner.setAdapter(spinnerAdapter);
        linearLayoutFormContainer.setVisibility(View.GONE);


        scanningText = (TextView) findViewById(R.id.scanningText);
        pulsatingView = (PulsatingView) findViewById(R.id.pulseContent);
        pulsatingView.setVisibility(View.VISIBLE);
        pulsatingView.startPulseAnimation();


        btnAddUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url = NetworkUtils.getUrlForPost();
                JSONObject json = new JSONObject();
                try{
                    json.put(NetworkUtils.UID_FIELD,inputUID.getText().toString().toLowerCase().trim());
                    json.put(NetworkUtils.NAME_FIELD,inputName.getText().toString().trim());
                    json.put(NetworkUtils.LAST_NAME_FIELD,inputLName.getText().toString().trim());
                    json.put(NetworkUtils.RANK_FIELD,Integer.parseInt(rankChooserSpinner.getSelectedItem().toString()));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                NetworkUtils.sendPostRequest(getApplicationContext(),url,json,AddCardActivity.this);

            }
        });



        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this,new MyNfcListener(),NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK|NfcAdapter.FLAG_READER_NFC_A,null);




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //TODO:(1) HANDLE ORIENTATION CHANGE
    }

    @Override
    protected void onStop() {
        super.onStop();
        nfcAdapter.disableReaderMode(this);
    }

    @Override
    public void reponseHttpPostRequete(int result) {

        if(result == 0) {
            Snackbar.make(coordinatorLayout,getResources().getString(R.string.snackbar_usr_success),Snackbar.LENGTH_LONG).show();

        }
        else if(result == -1) {
            Snackbar.make(coordinatorLayout,getResources().getString(R.string.snackbar_usr_conflict),Snackbar.LENGTH_LONG).show();
        }
        else Snackbar.make(coordinatorLayout,getResources().getString(R.string.snackbar_usr_failed),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void reponseHttpGetAllRequete(JSONObject jObj) {

    }


    private class MyNfcListener implements NfcAdapter.ReaderCallback {

        @Override
        public void onTagDiscovered(Tag tag) {
            final Tag finTag = tag;
            Log.d(TAG,"UID DISCOVERED"+ (new ParseNFC()).uidHexToStr(tag.getId()));
            cardDiscovered(finTag.getId());



        }
        private void cardDiscovered(byte[] uid) {


            ParseNFC parser = new ParseNFC();
            final String strUid = parser.uidHexToStr(uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pulsatingView.animate().translationY(pulsatingView.getHeight()).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            pulsatingView.setVisibility(View.GONE);
                            linearLayoutFormContainer.setVisibility(View.VISIBLE);
                        }
                    });


                    inputUID.setText(strUid);
                    inputUID.setFocusable(false);
                    inputUID.setClickable(false);


                }
            });



        }
    }
}
