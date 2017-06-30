package com.example.lasse.nfcinterface.utils;

/**
 * Created by lasse on 30/06/17.
 */

public class ParseNFC {

    private byte[] uid;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public ParseNFC() {

    }
    public ParseNFC(byte[] id) {
        this.uid = id;
    }

    public String uidHexToStr() {
        char[] hexChars = new char[this.uid.length * 2];
        for ( int j = 0; j < this.uid.length; j++ ) {
            int v = this.uid[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
