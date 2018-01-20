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

import android.widget.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.text.DecimalFormat;
import android.view.View;



public class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {


    protected TextView total_text;

    protected double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        Model[] menuItems = new Model[11];
        menuItems[0] = new Model("Pizza", 7.99);
        menuItems[1] = new Model("Burger", 9.99);
        menuItems[2] = new Model("Coke", 1.99);
        menuItems[3] = new Model("Burger", 9.99);
        menuItems[4] = new Model("Coke", 1.99);
        menuItems[5] = new Model("Burger", 9.99);
        menuItems[6] = new Model("Coke", 1.99);
        menuItems[7] = new Model("Burger", 9.99);
        menuItems[8] = new Model("Coke", 1.99);
        menuItems[9] = new Model("Burger", 9.99);
        menuItems[10] = new Model("Coke", 1.99);
        populateList(menuItems);

    }

    protected void populateList(Model[] menuItems) {

        ListView listView = findViewById(R.id.recipe_list_view);

        CustomAdapter adapter = new CustomAdapter(this, menuItems, this);

        listView.setAdapter(adapter);

        total_text = findViewById(R.id.total);

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(NFCActivity.this).create();
                alertDialog.setTitle("Would you like to add a tip?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add selected amount",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }

    public void updateTotal() {
        if (totalPrice < 0) {
            totalPrice = 0;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        total_text.setText("Total: £" + df.format(totalPrice));
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