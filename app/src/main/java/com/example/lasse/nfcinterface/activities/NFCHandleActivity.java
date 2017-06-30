package com.example.lasse.nfcinterface.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.utils.ParseNFC;

public class NFCHandleActivity extends AppCompatActivity {
    private final static String TAG = NFCHandleActivity.class.getSimpleName();

    public final static String UID_DETECTED_STRING = "121545856";
    public final static String NFCCLASS_ID_CALLING = TAG;
    private TextView uidScanned;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfchandle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uidScanned =  (TextView) findViewById(R.id.uidScanned);
        NfcAdapter nfcadapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = getIntent();
        if(intent != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            if(nfcadapter != null) {
                Log.d(TAG, "***************************detected mifare classic CARD -----------------------------------------------");

                byte lowid[] = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Intent intentDb = new Intent(NFCHandleActivity.this,MainActivity.class);

                ParseNFC parser = new ParseNFC(lowid);
                String uidstr = parser.uidHexToStr();
                intentDb.putExtra(UID_DETECTED_STRING,uidstr);
                intentDb.putExtra(NFCCLASS_ID_CALLING,TAG);

                Log.i(TAG, "UID------- " + uidstr.toLowerCase());
                uidScanned.setText(uidstr.toLowerCase().trim());

                startActivity(intentDb);


            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"***************************detected mifare classic CARD -----------------------------------------------");
        if(intent != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.d(TAG,"***************************detected mifare classic CARD -----------------------------------------------");
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG);
            byte lowid[] = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            for(int i=0;i<lowid.length;i++) {
                Log.i(TAG,"UID------- "+lowid[i]);
            }
            Log.d(TAG,"PARCELABLE TAG CONTENT-----"+tag.toString());
            if(rawMessages != null) {
                    System.out.println("SIZE OF RAW MESSAGE :"+rawMessages.length);
                Log.d(TAG,"SIZE OF RAW MESSAGE----------"+rawMessages.length);
            }

        }
    }
}
