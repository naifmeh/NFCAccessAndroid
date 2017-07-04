package com.example.lasse.nfcinterface.nfc;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

/**
 * Created by lasse on 04/07/17.
 */

public class MyHostApduService extends HostApduService {
    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        return new byte[0];
    }

    @Override
    public void onDeactivated(int i) {

    }
}
