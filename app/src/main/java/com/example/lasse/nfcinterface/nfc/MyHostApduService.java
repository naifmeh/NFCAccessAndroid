package com.example.lasse.nfcinterface.nfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lasse.nfcinterface.R;
import com.example.lasse.nfcinterface.utils.HandleEmulator;

import java.util.Arrays;

/**
 * Created by lasse on 04/07/17.
 */

public class MyHostApduService extends HostApduService {

    private static final String TAG = MyHostApduService.class.getSimpleName();

    private static final String CARD_AID = "F0010203040506";

    private static final String SELECT_APDU_HEADER = "00A40400";

    private static final byte[]  SELECT_OK_SW  = HexStringToByteArray("9000");
    private static final byte[]  UNKNOWND_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu(CARD_AID);

    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    //TODO HANDLE THE CASE WHERE THE USER ISNT PRESENT IN THE SHAREDPREFS
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        Log.i(TAG, "Received APDU: " + ByteArrayToHexString(apdu));
        byte[] uidBytes={};

        if (Arrays.equals(SELECT_APDU, apdu)) {
            String uid = HandleEmulator.getUidFromSharedPrefs(this);
            if (uid != null) {
                uidBytes = HexStringToByteArray(uid.toUpperCase());
            }
            if(uidBytes.length > 2)
                return  ConcatArrays(uidBytes,SELECT_OK_SW);
            else return UNKNOWND_CMD_SW;
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.notConnected),Toast.LENGTH_SHORT).show();
            return UNKNOWND_CMD_SW;
        }

    }

    @Override
    public void onDeactivated(int reason) {

        Log.i(TAG, "Deactivated: " + reason);
        Toast.makeText(getApplicationContext(),getString(R.string.nfcLinkBroken),Toast.LENGTH_SHORT).show();
    }
}
