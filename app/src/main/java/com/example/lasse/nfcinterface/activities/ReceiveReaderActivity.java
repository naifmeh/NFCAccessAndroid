package com.example.lasse.nfcinterface.activities;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.lasse.nfcinterface.R;

public class ReceiveReaderActivity extends AppCompatActivity {
    private static final String TAG = ReceiveReaderActivity.class.getSimpleName();
    TextView mInfoTxtVw;
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_reader);
        mInfoTxtVw = (TextView) findViewById(R.id.infosNFC);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(final Tag tag) {
                Log.d(TAG,"TAG DISCOVERED");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInfoTxtVw.setText(tag.getId().toString());
                    }
                });
            }

        },NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK|NfcAdapter.FLAG_READER_NFC_F,null);
    }
}
