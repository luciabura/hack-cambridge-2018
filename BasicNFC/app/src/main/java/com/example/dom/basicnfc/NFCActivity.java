package com.example.dom.basicnfc;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mEditText = (EditText) findViewById(R.id.edit_text_field);

        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            mEditText.setText("Sorry this device does not have NFC.");
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, this);


//        //example of getting individual items from the output
//        try {
//            //String jsonoutput = menu.getString("ccnum");
//            //Log.w("test string output", jsonoutput);
//            JSONArray jsonoutput = menu.getJSONArray("food");
//            String food = (String) jsonoutput.get(0);
//            //String food = (String) jsonoutput.get(1);
//            Log.w("test string output", food);
//        }
//        catch(JSONException e){Log.w("error", "errormsg");}
    }

    /**
     * Ndef Record that will be sent over via NFC. MESSAGE GETS SET UP HERE.
     * @param nfcEvent
     * @return
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //String message = mEditText.getText().toString();
        //Make the JSON Object
        JSONObject menu = new JSONObject();
        try {
            JSONArray foodlist = new JSONArray();
            foodlist.put("fish");
            foodlist.put("bread");
            JSONArray pricelist = new JSONArray();
            pricelist.put(7.90);
            pricelist.put(8.87);
            menu.put("food", foodlist);
            menu.put("price", pricelist);
            menu.put("ccnum", JSONObject.NULL);
        }
        catch(JSONException e){}


        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", menu.toString().getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        Log.w("JSON Message about to be passed (within NFC Range)", menu.toString());
        return ndefMessage;
    }
}