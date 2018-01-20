package com.example.dom.basicnfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mshrestha on 7/23/2014.
 */
public class NFCDisplayActivity extends Activity {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_display);
        mTextView = (TextView) findViewById(R.id.text_view);
        //Log.w("sent message?", "now");
    }

    /*
    MESSAGE GETS RECEIVED AND RECEIVING TEXT BOX UPDATED
     */
    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            String recvd_string = new String(message.getRecords()[0].getPayload());
            mTextView.setText(recvd_string);
            Log.w("Message received", recvd_string);

            //CONVERT THE JSON TEXT INTO A JSON OBJECT
            try {
                JSONObject menu = new JSONObject(recvd_string);
                Log.w("JSON output received and loaded", menu.toString(4));
            } catch (JSONException e) {
            }

            //Sums up total price
//            for (int i = 0; i < menu.names().length(); i++) {
//                    try {
//                        result = result + Integer.parseInt(jSonObj.names().getString(i));
//                    } catch (JSONException e) {
//                    }
//                }
//            }

        } else
            mTextView.setText("Waiting for NDEF Message");

    }
}
